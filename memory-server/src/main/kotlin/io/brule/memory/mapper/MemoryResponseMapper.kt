package io.brule.memory.mapper

import io.brule.memory.api.MemoryDto
import io.brule.memory.api.MemoryMetadataDto
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberResponse
import io.brule.memory.api.MemoryTagDto
import io.brule.memory.dto.*
import qdrant.client.grpc.points.ScrollResponse
import qdrant.client.grpc.points.SearchResponse

object MemoryResponseMapper {
    fun fromSearchResponse(grpcResponse: SearchResponse, page: Int, pageSize: Int): MemoryRecallResponse {
        val memories = grpcResponse.resultList.map { scoredPoint ->
            // extract payload map
            val payload = scoredPoint.payloadMap
            val content = payload["content"]?.stringValue ?: ""
            val summary = payload["summary"]?.stringValue ?: ""
            val uid = payload["uid"]?.stringValue ?: ""
            val createdAt = payload["created_at"]?.stringValue ?: ""

            // parse tags list
            val tags = payload["tags"]
                ?.listValue
                ?.valuesList
                ?.mapNotNull { value ->
                    value.structValue?.let { struct ->
                        val fields = struct.fieldsMap
                        val name = fields["name"]?.stringValue
                        val v = fields["value"]?.stringValue
                        if (name != null && v != null) MemoryTagDto(name, v) else null
                    }
                } ?: emptyList()

            MemoryDto(
                content = content,
                metadata = MemoryMetadataDto(
                    tags = tags,
                    summary = summary,
                    uid = uid,
                    createdAt = createdAt
                )
            )
        }

        return MemoryRecallResponse(
            memories = memories,
            totalCount = memories.size,
            page = page,
            pageSize = pageSize
        )
    }

    fun fromUpsertResponse(grpcResponse: Any): MemoryRememberResponse {
        return MemoryRememberResponse(acknowledged = true)
    }


    fun fromScrollResponse(scrollResponse: ScrollResponse, page: Int, pageSize: Int): MemoryRecallResponse {
        val memories = scrollResponse.resultList.map { retrievedPoint ->
            // extract payload map
            val payload = retrievedPoint.payloadMap
            val content = payload["content"]?.stringValue ?: ""
            val summary = payload["summary"]?.stringValue ?: ""
            val uid = payload["uid"]?.stringValue ?: ""
            val createdAt = payload["created_at"]?.stringValue ?: ""

            // parse tags list
            val tags = payload["tags"]
                ?.listValue
                ?.valuesList
                ?.mapNotNull { value ->
                    value.structValue?.let { struct ->
                        val fields = struct.fieldsMap
                        val name = fields["name"]?.stringValue
                        val v = fields["value"]?.stringValue
                        if (name != null && v != null) MemoryTagDto(name, v) else null
                    }
                } ?: emptyList()

            MemoryDto(
                content = content,
                metadata = MemoryMetadataDto(
                    tags = tags,
                    summary = summary,
                    uid = uid,
                    createdAt = createdAt
                )
            )
        }

        return MemoryRecallResponse(
            memories = memories,
            totalCount = memories.size,
            page = page,
            pageSize = pageSize
        )
    }

}