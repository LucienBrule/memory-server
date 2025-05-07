package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.config.MemoryConfig
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.jboss.logging.Logger
import qdrant.client.grpc.QdrantGrpc
import qdrant.client.grpc.collections.CollectionsGrpc
import qdrant.client.grpc.points.service.PointsGrpc

@ApplicationScoped
class QdrantClientProvider(
    val config: MemoryConfig,
    val logger: Logger,
) {

    @Produces
    fun qdrantChannel(): ManagedChannel {
        logger.info("Creating Qdrant channel")
        logger.info(
            "Qdrant config :: " +
                    "host=${config.qdrant().host()} " +
                    "port=${config.qdrant().port()} " +
                    "secure=${config.qdrant().secure()} " +
                    "apiKey=${config.qdrant().apiKey().takeLast(4)}"
        )
        val builder = ManagedChannelBuilder.forAddress(config.qdrant().host(), config.qdrant().port())
        return if (config.qdrant().secure()) {
            logger.info("Using secure Qdrant channel")
            builder.useTransportSecurity().build()
        } else {
            logger.info("Using insecure Qdrant channel")
            builder.usePlaintext().build()
        }
    }

    @Produces
    fun qdrantBlockingStub(channel: ManagedChannel): QdrantGrpc.QdrantBlockingStub =
        QdrantGrpc.newBlockingStub(
            ClientInterceptors.intercept(
                channel,
                QdrantApiKeyInterceptor(config.qdrant().apiKey())
            )
        )

    @Produces
    fun pointsClient(channel: ManagedChannel): PointsGrpc.PointsBlockingStub =
        PointsGrpc.newBlockingStub(
            ClientInterceptors.intercept(
                channel,
                QdrantApiKeyInterceptor(config.qdrant().apiKey())
            )
        )

    @Produces
    fun collectionsClient(channel: ManagedChannel): CollectionsGrpc.CollectionsBlockingStub =
        CollectionsGrpc.newBlockingStub(
            ClientInterceptors.intercept(
                channel,
                QdrantApiKeyInterceptor(config.qdrant().apiKey())
            )
        )
}