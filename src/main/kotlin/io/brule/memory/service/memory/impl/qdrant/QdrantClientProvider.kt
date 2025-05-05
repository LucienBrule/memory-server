package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.config.MemoryConfig
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import qdrant.client.grpc.QdrantGrpc
import qdrant.client.grpc.collections.CollectionsGrpc
import qdrant.client.grpc.points.service.PointsGrpc

@ApplicationScoped
class QdrantClientProvider(
    val config: MemoryConfig
) {

    @Produces
    fun qdrantChannel(): ManagedChannel =
        ManagedChannelBuilder.forAddress(config.qdrant().host(), config.qdrant().port())
            .usePlaintext()
            .build()

    @Produces
    fun qdrantBlockingStub(channel: ManagedChannel): QdrantGrpc.QdrantBlockingStub =
        QdrantGrpc.newBlockingStub(channel)

    @Produces
    fun pointsClient(channel: ManagedChannel): PointsGrpc.PointsBlockingStub =
        PointsGrpc.newBlockingStub(channel)

    @Produces
    fun collectionsClient(channel: ManagedChannel): CollectionsGrpc.CollectionsBlockingStub =
        CollectionsGrpc.newBlockingStub(channel)
}