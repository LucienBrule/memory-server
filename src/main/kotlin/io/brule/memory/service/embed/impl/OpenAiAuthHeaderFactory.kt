package io.brule.memory.service.embed.impl

import io.brule.memory.config.MemoryConfig
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap
import org.eclipse.microprofile.config.inject.ConfigProperty
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