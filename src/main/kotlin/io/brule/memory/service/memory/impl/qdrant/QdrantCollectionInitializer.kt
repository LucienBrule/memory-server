package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.config.MemoryConfig
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger
import qdrant.client.grpc.collections.CollectionsGrpc
import qdrant.client.grpc.collections.CreateCollection
import qdrant.client.grpc.collections.Distance
import qdrant.client.grpc.collections.HnswConfigDiff
import qdrant.client.grpc.collections.VectorParams
import qdrant.client.grpc.collections.VectorParamsMap
import qdrant.client.grpc.collections.VectorsConfig

@ApplicationScoped
class QdrantCollectionInitializer(
    private val collections: CollectionsGrpc.CollectionsBlockingStub,
    private val config: MemoryConfig,
    private val logger: Logger
) {

    fun onStart(@Observes event: StartupEvent) {
        logger.info("Initializing Qdrant collection ${config.qdrant().collectionName()}")
        val collectionName = config.qdrant().collectionName()
        try {
            createCollection(collectionName)
//            collections.collectionExists(
//                CollectionExistsRequest.newBuilder().setCollectionName(collectionName).build()
//            )
            logger.info("Qdrant collection $collectionName already exists")
            return
        } catch (e: StatusRuntimeException) {
            if (e.status.code == Status.Code.NOT_FOUND) {
                logger.info("Creating Qdrant collection $collectionName")
                createCollection(collectionName)
                logger.info("Qdrant collection $collectionName created")
            } else if(e.status.code == Status.Code.ALREADY_EXISTS){
                logger.info("Qdrant collection $collectionName already exists")
            }
            else {
                logger.error("Error checking Qdrant collection $collectionName", e)
                throw e
            }
        }
    }

    fun createCollection(collectionName: String) {
        collections.create(
            CreateCollection.newBuilder().setCollectionName(collectionName).setVectorsConfig(
                    VectorsConfig.newBuilder().setParamsMap(
                            VectorParamsMap.newBuilder().putMap(
                                    config.qdrant().vector().name(),
                                    VectorParams.newBuilder().setSize(config.qdrant().vector().size())
                                        .setDistance(Distance.Cosine).setHnswConfig(
                                            HnswConfigDiff.newBuilder().setM(16).setEfConstruct(200).build()
                                        ).build()
                                )
                        )
                ).build()

        )
    }
}