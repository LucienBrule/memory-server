package io.brule.memory.service.embed.impl.oai

import io.brule.memory.config.MemoryConfig
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory

@ApplicationScoped
class OpenAiAuthHeaderFactory(
    private val config: MemoryConfig
): ClientHeadersFactory {

    override fun update(
        incomingHeaders: MultivaluedMap<String, String>,
        clientOutgoingHeaders: MultivaluedMap<String, String>
    ): MultivaluedMap<String, String> {
        val map = MultivaluedHashMap<String, String>()
        map.add("Authorization", "Bearer ${config.openai().apiKey()}")
        return map
    }
}