package io.brule.memory.security

/**
 * Holds the name of the currently executing MCP tool for the current thread.
 */
object CurrentToolContext {
    private val toolName = ThreadLocal<String?>()

    /**
     * Sets the current tool name for this thread.
     */
    fun set(name: String) {
        toolName.set(name)
    }

    /**
     * Retrieves the current tool name, or null if none is set.
     */
    fun get(): String? {
        return toolName.get()
    }

    /**
     * Clears the current tool name from the thread-local storage.
     */
    fun clear() {
        toolName.remove()
    }
}