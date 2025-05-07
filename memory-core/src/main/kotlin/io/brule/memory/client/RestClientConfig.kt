package io.brule.memory.client

import io.brule.memory.config.MemoryConfig
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedMap
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory

@ApplicationScoped
class RestClientConfig: ClientHeadersFactory {

    @Inject
    private lateinit var config: MemoryConfig

    override fun update(
        incomingHeaders: MultivaluedMap<String, String>,
        clientOutgoingHeaders: MultivaluedMap<String, String>
    ): MultivaluedMap<String, String> {
        clientOutgoingHeaders.add("X-API-KEY", config.apiKey)
        return clientOutgoingHeaders
    }
}