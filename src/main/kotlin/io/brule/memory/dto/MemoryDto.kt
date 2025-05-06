package io.brule.memory.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a named tag used to filter or label memories.
 */
data class MemoryTagDto(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("value")
    val value: String
)

/**
 * Response returned when recalling memories.
 */
data class MemoryRecallResponse(
    @JsonProperty("memories")
    val memories: List<MemoryDto>,
    @JsonProperty("totalCount")
    val totalCount: Int,
    @JsonProperty("page")
    val page: Int,
    @JsonProperty("pageSize")
    val pageSize: Int
)

/**
 * Response returned after storing a new memory.
 */
data class MemoryRememberResponse(
    @JsonProperty("acknowledged")
    val acknowledged: Boolean = false,
)

/**
 * Metadata associated with a memory, including tags and optional fields.
 */
data class MemoryMetadataDto(
    @JsonProperty("tags")
    val tags: List<MemoryTagDto> = emptyList(),
    @JsonProperty("summary")
    val summary: String = "",
    @JsonProperty("uid")
    val uid: String = "",
    @JsonProperty("createdAt")
    val createdAt: String = ""
)

/**
 * Core memory data structure with content and metadata.
 */
data class MemoryDto @JsonCreator constructor(
    @JsonProperty("content")
    val content: String,

    @JsonProperty("metadata")
    val metadata: MemoryMetadataDto = MemoryMetadataDto()
)

/**
 * Request to store a new memory.
 */
data class MemoryRememberRequest(
    @JsonProperty("memory")
    val memory: MemoryDto
)

/**
 * Request to recall memories by content or tags.
 */
data class MemoryRecallRequest(
    @JsonProperty("memory")
    val memory: MemoryDto = MemoryDto(content = ""),
    @JsonProperty("exclude")
    val exclude: List<MemoryTagDto> = emptyList(),
    @JsonProperty("page")
    val page: Int = 1,
    @JsonProperty("pageSize")
    val pageSize: Int = 3
)
