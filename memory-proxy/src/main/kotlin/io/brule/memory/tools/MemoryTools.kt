package io.brule.memory.tools

import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.service.MemoryProxyService
import io.quarkiverse.mcp.server.Tool
import io.quarkiverse.mcp.server.ToolResponse
import io.brule.memory.security.ToolGuard
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class MemoryTools(
    private val proxyService: MemoryProxyService,
){
    /**
     * Store a new memory with its content and optional metadata.
     */
    @Tool(description = "Store a new memory with its content and optional tags")
    @ToolGuard(expectedName = "remember")
    fun remember(request: MemoryRememberRequest): ToolResponse =
        proxyService.remember(request)

    /**
     * Search for memories by content similarity or tag filtering.
     */
    @Tool(description = "Search for memories by content similarity or tag filtering")
    @ToolGuard(expectedName = "recall")
    fun recall(request: MemoryRecallRequest): ToolResponse =
        proxyService.recall(request)
}