package io.brule.memory.service

import io.brule.memory.api.MemoryApi
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberResponse
import io.brule.memory.security.InitTracker
import io.quarkiverse.mcp.server.ToolResponse
import io.quarkiverse.mcp.server.TextContent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jboss.logging.Logger
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class MemoryProxyService @Inject constructor(
    private val logger: Logger,
    private val memoryClient: MemoryApi,
    private val initTracker: InitTracker
) {

    fun remember(request: MemoryRememberRequest): ToolResponse {
        if (!initTracker.isReady()) {
            return ToolResponse.error("❌ Memory service not initialized. Try again shortly.")
        }
        return try {
            // Fill missing metadata defaults
            val metadata = request.memory.metadata
            val filledMetadata = metadata.copy(
                createdAt = metadata.createdAt.ifBlank { Instant.now().toString() },
                uid = metadata.uid.ifBlank { UUID.randomUUID().toString() }
            )
            val filledRequest = request.copy(
                memory = request.memory.copy(metadata = filledMetadata)
            )
            memoryClient.remember(filledRequest)
            ToolResponse.success(TextContent("✅ Memory stored with uid=${filledMetadata.uid}"))
        } catch (e: Exception) {
            logger.error("Failed to store memory", e)
            ToolResponse.error("❌ Failed to store memory: ${e.message}")
        }
    }

    fun recall(request: MemoryRecallRequest): ToolResponse {
        if (!initTracker.isReady()) {
            return ToolResponse.error("❌ Memory service not initialized. Try again shortly.")
        }
        return try {
            val response: MemoryRecallResponse = memoryClient.recall(request)
            ToolResponse.success(TextContent(response.toMarkdown()))
        } catch (e: Exception) {
            logger.error("Failed to recall memory", e)
            ToolResponse.error("❌ Recall failed: ${e.message}")
        }
    }

    private fun MemoryRecallResponse.toMarkdown(): String = buildString {
        appendLine("---")
        appendLine("title: Memory Recall Results")
        appendLine("totalCount: $totalCount")
        appendLine("page: $page, pageSize: $pageSize")
        appendLine("---\n")
        memories.forEachIndexed { idx, memory ->
            appendLine("### ${idx + 1}. UID: ${memory.metadata.uid}")
            appendLine("- **Content:** ${memory.content}")
            memory.metadata.summary.takeIf { it.isNotBlank() }?.let {
                appendLine("- **Summary:** $it")
            }
            memory.metadata.createdAt.takeIf { it.isNotBlank() }?.let {
                appendLine("- **Created At:** $it")
            }
            if (memory.metadata.tags.isNotEmpty()) {
                appendLine("- **Tags:**")
                memory.metadata.tags.forEach { tag ->
                    appendLine("  - ${tag.name}=${tag.value}")
                }
            }
            appendLine()
        }
    }
}