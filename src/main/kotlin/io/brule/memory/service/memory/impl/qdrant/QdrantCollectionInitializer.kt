package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.config.MemoryConfig
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger
import qdrant.client.grpc.collections.*

@ApplicationScoped
class QdrantCollectionInitializer(
    private val collections: CollectionsGrpc.CollectionsBlockingStub,
    private val config: MemoryConfig,
    private val logger: Logger
) {

    fun onStart(@Observes event: StartupEvent) {
        val collectionName = config.qdrant().collectionName()
        logger.info("""
            🧠 Initializing Qdrant collection
            - Name: $collectionName
            - Host: ${config.qdrant().host()}
            - Secure: ${config.qdrant().secure()}
            - Vector: ${config.qdrant().vector().name()} (${config.qdrant().vector().size()} dims)
        """.trimIndent())

        when (checkCollectionStatus(collectionName)) {
            CollectionStatus.NOT_FOUND -> {
                logger.info("Collection not found. Creating new collection: $collectionName")
                createCollection(collectionName)
                logger.info("Collection $collectionName created successfully.")
            }
            CollectionStatus.EXISTS -> {
                logger.info("Collection $collectionName already exists.")
            }
            is CollectionStatus.Failure -> {
                logger.error("Failed to check or create collection: ${collectionName}", (checkCollectionStatus(collectionName) as CollectionStatus.Failure).cause)
                throw (checkCollectionStatus(collectionName) as CollectionStatus.Failure).cause
            }
        }
    }

    private fun checkCollectionStatus(name: String): CollectionStatus {
        return try {
            collections.get(GetCollectionInfoRequest.newBuilder().setCollectionName(name).build())
            CollectionStatus.EXISTS
        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                Status.Code.NOT_FOUND -> CollectionStatus.NOT_FOUND
                else -> CollectionStatus.Failure(e)
            }
        }
    }

    private fun createCollection(name: String) {
        val vectorName = config.qdrant().vector().name()
        val vectorSize = config.qdrant().vector().size()

        val vectorParams = VectorParams.newBuilder()
            .setSize(vectorSize)
            .setDistance(Distance.Cosine)
            .setHnswConfig(HnswConfigDiff.newBuilder().setM(16).setEfConstruct(200))
            .build()

        val vectorsConfig = VectorsConfig.newBuilder()
            .setParamsMap(VectorParamsMap.newBuilder().putMap(vectorName, vectorParams))
            .build()

        val request = CreateCollection.newBuilder()
            .setCollectionName(name)
            .setVectorsConfig(vectorsConfig)
            .build()

        collections.create(request)
    }

    private sealed class CollectionStatus {
        object EXISTS : CollectionStatus()
        object NOT_FOUND : CollectionStatus()
        data class Failure(val cause: Throwable) : CollectionStatus()
    }
}