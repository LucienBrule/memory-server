package io.brule.memory

import io.brule.memory.dto.MemoryRecallRequest
import io.brule.memory.dto.MemoryRecallResponse
import io.brule.memory.dto.MemoryRememberRequest
import io.brule.memory.dto.MemoryRememberResponse
import io.brule.memory.service.memory.impl.qdrant.QdrantClientProvider
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import io.quarkiverse.mcp.server.Tool

@ApplicationScoped
class MemoryTools {

    /**
     * Qdrant gRPC client provider.
     */
    @Inject
    lateinit var qdrantClientProvider: QdrantClientProvider

    /**
     * Store a new memory with its content and optional metadata.
     */
    @Tool(description = "Store a new memory with its content and optional tags")
    fun remember(request: MemoryRememberRequest): MemoryRememberResponse {
        // TODO: implement Qdrant upsert logic using qdrantClientProvider
        throw UnsupportedOperationException("Not implemented yet")
    }

    /**
     * Search for memories by content similarity or tag filtering.
     */
    @Tool(description = "Search for memories by content similarity or tag filtering")
    fun recall(request: MemoryRecallRequest): MemoryRecallResponse {
        // TODO: implement Qdrant search/scroll logic using qdrantClientProvider
        throw UnsupportedOperationException("Not implemented yet")
    }
}