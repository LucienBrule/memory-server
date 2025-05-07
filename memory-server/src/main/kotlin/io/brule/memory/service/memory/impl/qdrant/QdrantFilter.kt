package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.api.MemoryRecallRequest
import qdrant.client.grpc.points.condition
import qdrant.client.grpc.points.fieldCondition
import qdrant.client.grpc.points.filter
import qdrant.client.grpc.points.match

object QdrantFilter {
    fun fromRequest(request: MemoryRecallRequest) = filter {
        must += request.memory.metadata.tags.map { tag ->
            condition {
                field = fieldCondition {
                    key = tag.name
                    match = match {
                        keyword = tag.value
                    }
                }
            }
        }
    }
}