package io.brule.memory.client

import io.brule.memory.api.MemoryApi
import io.brule.memory.config.MemoryConfig
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import java.net.URI

@ApplicationScoped
class RestClientProvider @Inject constructor(
    private val config: MemoryConfig,
    private val headersFactory: RestClientConfig,  // inject the real config factory bean
    private val errorMapper: RestClientErrorMapper // inject the real mapper bean
) {
    @Produces
    fun provideMemoryClient(): MemoryApi {
        // all of these are CDI-managed instances, with their @Inject fields populated
        return QuarkusRestClientBuilder.newBuilder()
            .baseUri(URI.create(config.endpoint))
            .register(headersFactory)
            .register(errorMapper)
            .build(HttpMemoryClient::class.java)
    }
}