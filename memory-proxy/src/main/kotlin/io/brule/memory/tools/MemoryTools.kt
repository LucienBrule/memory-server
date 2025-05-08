package io.brule.memory.tools

import io.brule.memory.api.MemoryApi

import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryRememberResponse
import io.quarkiverse.mcp.server.Tool
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class MemoryTools(
    private val memoryClient: MemoryApi,
){
    /**
     * Store a new memory with its content and optional metadata.
     */
    @Tool(description = "Store a new memory with its content and optional tags")
    fun remember(request: MemoryRememberRequest): MemoryRememberResponse = memoryClient.remember(request)

    /**
     * Search for memories by content similarity or tag filtering.
     */
    @Tool(description = "Search for memories by content similarity or tag filtering")
    fun recall(request: MemoryRecallRequest): MemoryRecallResponse = memoryClient.recall(request)
}