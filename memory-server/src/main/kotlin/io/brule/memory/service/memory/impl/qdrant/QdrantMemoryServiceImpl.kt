package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryRememberResponse
import io.brule.memory.config.MemoryConfig
import io.brule.memory.mapper.MemoryRequestMapper
import io.brule.memory.mapper.MemoryResponseMapper
import io.brule.memory.service.embed.EmbedService
import io.brule.memory.service.memory.MemoryService
import jakarta.enterprise.context.ApplicationScoped
import org.jboss.logging.Logger
import qdrant.client.grpc.points.ScrollPoints
import qdrant.client.grpc.points.SearchPoints
import qdrant.client.grpc.points.service.PointsGrpc
import java.util.*

@ApplicationScoped
class QdrantMemoryServiceImpl(
    private val points: PointsGrpc.PointsBlockingStub,
    private val config: MemoryConfig,
    private val embeddings: EmbedService,
    private val logger: Logger,
    private val indexClient: QdrantHttpIndexClient
) : MemoryService {

    init {
        indexClient.ensureAllIndexes()
    }

    companion object {
        // deterministic UUID namespace (choose any valid UUID)
        private val NAMESPACE_UUID: UUID = UUID.fromString("c8f4a7a4-0f67-4c2d-9b1b-71c5c3a7d123")
    }

    override fun remember(request: MemoryRememberRequest): MemoryRememberResponse {
        // ensure indexes for all tag keys in this memory
        val tagKeys = request.memory.metadata.tags.map { it.name }
        indexClient.ensureIndexes(tagKeys)
        // generate a deterministic UUID for the Qdrant point based on our namespace + our semantic UID
        val generatedUuid =
            UUID.nameUUIDFromBytes((NAMESPACE_UUID.toString() + request.memory.metadata.uid).toByteArray()).toString()
        val grpcRequest = MemoryRequestMapper.toUpsertPointsRequest(
            request = request,
            collectionName = config.qdrant().collectionName(),
            id = generatedUuid,
            vectorName = config.qdrant().vector().name(),
            vector = embeddings.embed(request.memory.content)
        )
        val grpcResponse = points.upsert(grpcRequest)
        return MemoryResponseMapper.fromUpsertResponse(grpcResponse)
    }

    override fun recall(request: MemoryRecallRequest): MemoryRecallResponse {
        logger.info("recalling memory with request: $request")

        // compute embedding once to reuse in initial and fallback searches
        val queryVector = embeddings.embed(request.memory.content)

        val hasTags = request.memory.metadata.tags.isNotEmpty()
        val hasContent = request.memory.content.isNotBlank()

        val grpcRequest = when {
            hasContent -> MemoryRequestMapper.toSearchPointsRequest(
                request = request,
                collectionName = config.qdrant().collectionName(),
                vectorName = config.qdrant().vector().name(),
                vector = queryVector
            )

            hasTags -> MemoryRequestMapper.toScrollPointsRequest(
                request = request,
                collectionName = config.qdrant().collectionName(),
                filter = QdrantFilter.fromRequest(request)
            )

            else -> error("Recall requires at least content or tags")
        }

        return when (grpcRequest) {
            is SearchPoints -> searchWithFallback(request, grpcRequest, queryVector)
            is ScrollPoints -> scrollWithFallback(request, grpcRequest, queryVector)
            else -> error("Invalid grpc request type")
        }
    }

    /**
     * Attempts a vector search and falls back by retrying without tag filters if needed.
     */
    private fun searchWithFallback(
        request: MemoryRecallRequest,
        searchRequest: SearchPoints,
        queryVector: List<Float>
    ): MemoryRecallResponse {
        try {
            val searchResponse = points.search(searchRequest)
            return MemoryResponseMapper.fromSearchResponse(searchResponse, request.page, request.pageSize)
        } catch (e: io.grpc.StatusRuntimeException) {
            if (e.status.code == io.grpc.Status.Code.PERMISSION_DENIED) {
                logger.warn("Search filter caused PERMISSION_DENIED; retrying search without tags", e)
                return fallbackToSearch(request, queryVector)
            }
            throw e
        }
    }

    /**
     * Attempts a scroll-based recall and falls back to vector search if filtering fails.
     */
    private fun scrollWithFallback(
        request: MemoryRecallRequest,
        scrollRequest: ScrollPoints,
        queryVector: List<Float>
    ): MemoryRecallResponse {
        try {
            val scrollResponse = points.scroll(scrollRequest)
            return MemoryResponseMapper.fromScrollResponse(scrollResponse, request.page, request.pageSize)
        } catch (e: io.grpc.StatusRuntimeException) {
            if (e.status.code == io.grpc.Status.Code.PERMISSION_DENIED) {
                logger.warn("Tag filter index missing; falling back to vector search", e)
                return fallbackToSearch(request, queryVector)
            }
            throw e
        }
    }

    /**
     * Fallback logic: perform vector search without tags.
     */
    private fun fallbackToSearch(
        request: MemoryRecallRequest,
        queryVector: List<Float>
    ): MemoryRecallResponse {
        val fallbackSearch = MemoryRequestMapper.toSearchPointsRequest(
            request = request.copy(
                memory = request.memory.copy(
                    metadata = request.memory.metadata.copy(tags = emptyList())
                )
            ),
            collectionName = config.qdrant().collectionName(),
            vectorName = config.qdrant().vector().name(),
            vector = queryVector
        )
        val fallbackResponse = points.search(fallbackSearch)
        return MemoryResponseMapper.fromSearchResponse(fallbackResponse, request.page, request.pageSize)
    }
}