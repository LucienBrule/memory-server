package io.brule.memory.mapper

import io.brule.memory.dto.MemoryDto
import io.brule.memory.dto.MemoryMetadataDto
import io.brule.memory.dto.MemoryRecallResponse
import io.brule.memory.dto.MemoryRememberResponse

import qdrant.client.grpc.points.SearchResponse
import io.brule.memory.dto.MemoryTagDto
import qdrant.client.grpc.points.ScrollResponse

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
                        val v    = fields["value"]?.stringValue
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
        return MemoryRememberResponse(
            memory = MemoryDto(
                content = "",
                metadata = MemoryMetadataDto(
                    uid = "",
                    createdAt = "",
                    summary = "",
                    tags = listOf()
                )
            )
        )
    }

    fun fromScrollResponse(scrollResponse: ScrollResponse, page: Int, pageSize: Int): MemoryRecallResponse {
       return MemoryRecallResponse(
           memories = listOf(
               MemoryDto(
                   content = "",
                   metadata = MemoryMetadataDto(
                       uid = "",
                       createdAt = "",
                       summary = "",
                       tags = listOf()
                   )
               )
           ),
           totalCount = 1,
           page = 1,
           pageSize = 1
       )
    }
}