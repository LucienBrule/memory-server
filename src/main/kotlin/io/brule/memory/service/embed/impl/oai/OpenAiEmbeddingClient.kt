package io.brule.memory.service.embed.impl.oai

import io.brule.memory.dto.OpenAiEmbeddingRequest
import io.brule.memory.dto.OpenAiEmbeddingResponse
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@Path("/v1/embeddings")
@RegisterRestClient(configKey = "openai")
@RegisterClientHeaders(OpenAiAuthHeaderFactory::class)
@RegisterProvider(OpenAiErrorMapper::class)
interface OpenAiEmbeddingClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun embed(request: OpenAiEmbeddingRequest): OpenAiEmbeddingResponse
}