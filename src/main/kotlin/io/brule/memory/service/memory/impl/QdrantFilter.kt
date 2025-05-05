package io.brule.memory.service.memory.impl

import io.brule.memory.dto.MemoryRecallRequest
import qdrant.client.grpc.points.Filter
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