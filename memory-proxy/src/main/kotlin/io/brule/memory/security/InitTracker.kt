package io.brule.memory.security

import io.quarkiverse.mcp.server.McpConnection
import io.quarkiverse.mcp.server.Notification
import io.quarkiverse.mcp.server.Notification.Type
import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class InitTracker {

    private val initialized = AtomicBoolean(false)

    fun isReady(): Boolean = initialized.get()

    @Notification(Type.INITIALIZED)
    fun onInitialized(connection: McpConnection) {
        initialized.set(true)
    }
}