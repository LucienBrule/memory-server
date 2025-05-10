package io.brule.memory.security

import io.quarkiverse.mcp.server.ToolResponse
import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.interceptor.AroundInvoke
import jakarta.interceptor.Interceptor
import jakarta.interceptor.InvocationContext
import org.jboss.logging.Logger

@Interceptor
@ToolGuard
@Priority(Interceptor.Priority.APPLICATION)
class ToolGuardInterceptor {

    @Inject
    lateinit var logger: Logger

    @AroundInvoke
    fun enforceToolName(context: InvocationContext): Any? {
        // 1. Locate our @ToolGuard annotation on method or class
        var guard = context.method.getAnnotation(ToolGuard::class.java)
        if (guard == null) {
            guard = context.target
                ?.javaClass
                ?.getAnnotation(ToolGuard::class.java)
        }
        requireNotNull(guard) {
            "ToolGuard annotation not found on ${context.method.name} or ${context.target?.javaClass}"
        }
        val expected = guard.expectedName

        // 2. Pull the real tool name from thread-local context
        val actual = CurrentToolContext.get() ?: "<unknown>"

        return if (actual == expected) {
            context.proceed()
        } else {
            logger.warn("❌ ToolGuard blocked unexpected tool call: expected=$expected, actual=$actual")
            ToolResponse.error("Tool '$actual' not recognized by this extension.")
        }
    }
}