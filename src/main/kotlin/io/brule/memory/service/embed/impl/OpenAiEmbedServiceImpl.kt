package io.brule.memory.service.embed.impl

import io.brule.memory.config.MemoryConfig
import io.brule.memory.dto.OpenAiEmbeddingRequest
import io.brule.memory.service.embed.EmbedService
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient

@ApplicationScoped
class OpenAiEmbedServiceImpl(
    @RestClient private val client: OpenAiEmbeddingClient,
    private val config: MemoryConfig
) : EmbedService {
    override fun embed(content: String): List<Double> {
        val embedResponse = client.embed(
            OpenAiEmbeddingRequest(
                model = config.openai().embedding().model(),
                input = content,
                encodingFormat = config.openai().embedding().encodingFormat()
            )
        )
        return embedResponse.data.first().embedding
    }
}