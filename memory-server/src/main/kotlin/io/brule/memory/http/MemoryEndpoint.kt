package io.brule.memory.http

import io.brule.memory.api.MemoryApi
import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryRememberResponse
import io.brule.memory.service.memory.MemoryService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger

@ApplicationScoped
class MemoryEndpoint(
    private val logger: Logger,
    private val memory: MemoryService
):MemoryApi{

    override fun remember(request: MemoryRememberRequest): MemoryRememberResponse{
        logger.info("Received request to /dev/remember")
        logger.info("request is: $request")

        val response: MemoryRememberResponse = memory.remember(request)

        logger.info("response is: $response")
        return response
    }

    override fun recall(request: MemoryRecallRequest): MemoryRecallResponse{
        logger.info("Received reqeust to /dev/recall")
        logger.info("request is $request")

        val response: MemoryRecallResponse = memory.recall(request)

        logger.info("response is $response")

        return response
    }
}