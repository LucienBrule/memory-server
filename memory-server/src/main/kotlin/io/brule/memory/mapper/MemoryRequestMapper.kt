package io.brule.memory.mapper

import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRememberRequest
import qdrant.client.grpc.points.Filter
import qdrant.client.grpc.points.UpsertPoints
import qdrant.client.grpc.points.SearchPoints
import qdrant.client.grpc.points.ScrollPoints
import qdrant.client.grpc.points.upsertPoints
import qdrant.client.grpc.points.searchPoints
import qdrant.client.grpc.points.scrollPoints
import qdrant.client.grpc.points.pointStruct
import qdrant.client.grpc.points.pointId
import qdrant.client.grpc.points.vectors
import qdrant.client.grpc.points.namedVectors
import qdrant.client.grpc.points.vector
import qdrant.client.grpc.points.withPayloadSelector

/**
 * Mapper for converting internal memory requests into Qdrant gRPC messages.
 */
object MemoryRequestMapper {

    /**
     * Convert a MemoryRememberRequest into a Qdrant UpsertPoints request.
     */
    fun toUpsertPointsRequest(
        request: MemoryRememberRequest,
        collectionName: String,
        id: String,
        vectorName: String,
        vector: List<Double>
    ): UpsertPoints = upsertPoints {
        // MUST use this.collectionName instead of collectionName because of scope resolution
        this.collectionName = collectionName

        // correct
        points += pointStruct {
            // MUST use this.id instead of id because of scope resolution
            this.id = pointId { uuid = id }

//            // correct
//            vectors {
//                //correct
//                namedVectors {
//                    // correct
//                    vectors[vectorName] = vector {
//                        vector.forEach { data += it.toFloat() }
//                    }
//                }
//            }
            this.vectors = vectors {
                this@vectors.vectors = namedVectors {
                    this@namedVectors.vectors[vectorName] = vector {
                        data += vector.map(Double::toFloat)
                    }
                }
            }
            // correct
           this.payload.putAll(request.memory.toPayloadMap())
        }
    }

    /**
     * Convert a MemoryRecallRequest into a Qdrant SearchPoints request
     * for semantic recall based on content similarity.
     */
    fun toSearchPointsRequest(
        request: MemoryRecallRequest,
        collectionName: String,
        vectorName: String,
        vector: List<Double>,
        limit: Int = request.pageSize * 2
    ): SearchPoints = searchPoints {
        // MUST use this.* because of scope resolution
        //correct
        this.collectionName = collectionName
        // correct
        this.vectorName = vectorName
        // correct
        this.limit = limit.toLong()
        // correct
        this.offset = ((request.page - 1) * request.pageSize).toLong()
        // we're missing setting the vector here.
        vector.forEach { this.vector += it.toFloat() }

        withPayload = withPayloadSelector {
            enable = true

        }

    }

    /**
     * Convert a MemoryRecallRequest into a Qdrant ScrollPoints request
     * for tag-only or combined recall.
     */
    fun toScrollPointsRequest(
        request: MemoryRecallRequest,
        collectionName: String,
        filter: Filter
    ): ScrollPoints = scrollPoints {
        // MUST use this.* because of scope resolution
        // correct
        this.collectionName = collectionName
        // correct
        this.limit = request.pageSize
        // correct
        this.offset = pointId { num = ((request.page - 1) * request.pageSize).toLong() }
        // correct
        this.filter = filter
    }
}
