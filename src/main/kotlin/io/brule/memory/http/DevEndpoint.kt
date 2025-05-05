package io.brule.memory.http

import io.brule.memory.dto.MemoryRecallRequest
import io.brule.memory.dto.MemoryRecallResponse
import io.brule.memory.dto.MemoryRememberRequest
import io.brule.memory.dto.MemoryRememberResponse
import io.brule.memory.service.memory.MemoryService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger

@ApplicationScoped
@Path("/api/dev")
class DevEndpoint(
    private val logger: Logger,
    private val memory: MemoryService
){

    @POST
    @Path("/remember")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun remember(request: MemoryRememberRequest): MemoryRememberResponse{
        logger.info("Received request to /dev/remember")
        logger.info("request is: $request")

        val response: MemoryRememberResponse = memory.remember(request)

        logger.info("response is: $response")
        return response
    }

    @POST
    @Path("/recall")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun recall(request: MemoryRecallRequest): MemoryRecallResponse{
        logger.info("Received reqeust to /dev/recall")
        logger.info("request is $request")

        val response: MemoryRecallResponse = memory.recall(request)

        logger.info("response is $response")

        return response
    }
}