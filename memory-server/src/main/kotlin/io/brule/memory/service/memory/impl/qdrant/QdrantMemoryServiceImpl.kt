package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.config.MemoryConfig
import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryRememberResponse
import io.brule.memory.mapper.MemoryRequestMapper
import io.brule.memory.mapper.MemoryResponseMapper
import io.brule.memory.service.embed.EmbedService
import io.brule.memory.service.memory.MemoryService
import jakarta.enterprise.context.ApplicationScoped
import org.jboss.logging.Logger
import qdrant.client.grpc.points.ScrollPoints
import qdrant.client.grpc.points.SearchPoints
import qdrant.client.grpc.points.service.PointsGrpc
import java.util.UUID

@ApplicationScoped
class QdrantMemoryServiceImpl(
    private val points: PointsGrpc.PointsBlockingStub,
    private val config: MemoryConfig,
    private val embeddings: EmbedService,
    private val logger: Logger
): MemoryService {

    companion object {
        // deterministic UUID namespace (choose any valid UUID)
        private val NAMESPACE_UUID: UUID = UUID.fromString("c8f4a7a4-0f67-4c2d-9b1b-71c5c3a7d123")
    }

    override fun recall(request: MemoryRecallRequest): MemoryRecallResponse {
        logger.info("recalling memory with request: $request")
        val grpcRequest = if (request.memory.metadata.tags.isEmpty()) {
            MemoryRequestMapper.toSearchPointsRequest(
                request=request,
                collectionName=config.qdrant().collectionName(),
                vectorName=config.qdrant().vector().name(),
                vector=embeddings.embed(request.memory.content)
            )
        } else {
            MemoryRequestMapper.toScrollPointsRequest(
                request = request,
                collectionName = config.qdrant().collectionName(),
                filter = QdrantFilter.fromRequest(request)
            )
        }
        return if (request.memory.metadata.tags.isEmpty()) {
            val searchResponse = points.search(grpcRequest as SearchPoints)
            MemoryResponseMapper.fromSearchResponse(searchResponse, request.page, request.pageSize)
        } else {
            val scrollResponse = points.scroll(grpcRequest as ScrollPoints)
            MemoryResponseMapper.fromScrollResponse(scrollResponse, request.page, request.pageSize)
        }
    }

    override fun remember(request: MemoryRememberRequest): MemoryRememberResponse {
        // generate a deterministic UUID for the Qdrant point based on our namespace + our semantic UID
        val generatedUuid = UUID.nameUUIDFromBytes((NAMESPACE_UUID.toString() + request.memory.metadata.uid).toByteArray()).toString()
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
}