package io.brule.memory.security

import jakarta.inject.Singleton
import jakarta.annotation.Priority
import jakarta.enterprise.inject.Alternative
import io.quarkiverse.mcp.server.runtime.McpMessageHandler
import io.quarkiverse.mcp.server.stdio.runtime.StdioMcpMessageHandler
import io.quarkiverse.mcp.server.runtime.ConnectionManager
import io.quarkiverse.mcp.server.runtime.*
import io.quarkiverse.mcp.server.runtime.ResponseHandlers
import io.quarkiverse.mcp.server.runtime.McpMetadata
import io.quarkiverse.mcp.server.runtime.config.McpRuntimeConfig
import io.quarkus.runtime.Quarkus
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintStream
import java.util.concurrent.Executors

@Singleton
@Alternative
@Priority(1)
class CustomStdioMcpMessageHandler(
    config: McpRuntimeConfig,
    connectionManager: ConnectionManager,
    promptManager: PromptManagerImpl,
    toolManager: ToolManagerImpl,
    resourceManager: ResourceManagerImpl,
    promptCompleteManager: PromptCompletionManagerImpl,
    resourceTemplateManager: ResourceTemplateManagerImpl,
    resourceTemplateCompleteManager: ResourceTemplateCompleteManagerImpl,
    initManager: NotificationManagerImpl,
    serverRequests: ResponseHandlers,
    metadata: McpMetadata,
    vertx: Vertx
) : StdioMcpMessageHandler(
    config,
    connectionManager,
    promptManager,
    toolManager,
    resourceManager,
    promptCompleteManager,
    resourceTemplateManager,
    resourceTemplateCompleteManager,
    initManager,
    serverRequests,
    metadata,
    vertx
) {
    override fun initialize(stdout: PrintStream, config: McpRuntimeConfig) {
        super.initialize(stdout, config)
        // nothing to change here
    }

    override fun handle(
        message: JsonObject,
        connection: McpConnectionBase,
        sender: Sender,
        securitySupport: SecuritySupport
    ) {
        // before dispatch, extract the method
        val rawMethod = message.getString("method", "")
        if (rawMethod.startsWith("tool/")) {
            CurrentToolContext.set(rawMethod.removePrefix("tool/"))
        } else {
            CurrentToolContext.clear()
        }
        try {
            super.handle(message, connection, sender, securitySupport)
        } finally {
            CurrentToolContext.clear()
        }
    }
}


/*
    public void handle(JsonObject message, McpConnectionBase connection, Sender sender, SecuritySupport securitySupport) {
        if (Messages.isResponse(message)) {
            // Response from a client
            responseHandlers.handleResponse(message.getValue("id"), message);
        } else {
            switch (connection.status()) {
                case NEW -> initializeNew(message, sender, connection, securitySupport);
                case INITIALIZING -> initializing(message, sender, connection, securitySupport);
                case IN_OPERATION -> operation(message, sender, connection, securitySupport);
                case CLOSED -> sender.send(
                        Messages.newError(message.getValue("id"), JsonRPC.INTERNAL_ERROR, "Connection is closed"));
            }
        }
    }
 */