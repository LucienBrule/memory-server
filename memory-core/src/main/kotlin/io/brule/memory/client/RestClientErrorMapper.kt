package io.brule.memory.client

import io.quarkus.arc.Unremovable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper
import org.jboss.logging.Logger

@Provider
@ApplicationScoped
class RestClientErrorMapper: ResponseExceptionMapper<RuntimeException> {

    @Inject
    lateinit var logger: Logger

    override fun toThrowable(response: Response): RuntimeException? {
        logger.error("Error calling remote service: ${response.status} ${response.statusInfo.reasonPhrase}")
        logger.error("Response body: ${response.readEntity(String::class.java)}")
        logger.error("Response headers: ${response.headers}")
        if (response.statusInfo.family == Response.Status.Family.SUCCESSFUL) {
            return null
        }
        return when (response.status) {
            400 -> RuntimeException("Bad request!")
            401 -> RuntimeException("Unauthorized!")
            403 -> RuntimeException("Forbidden!")
            404 -> RuntimeException("Not found!")
            410 -> RuntimeException("Gone!")
            420 -> RuntimeException("Enhance your calm!")
            429 -> RuntimeException("Rate limit exceeded!")
            500 -> RuntimeException("Internal server error!")
            503 -> RuntimeException("Service unavailable!")
            504 -> RuntimeException("Gateway timeout!")
            else -> RuntimeException("Unknown error! Status code: ${response.status} ${response.statusInfo.reasonPhrase}")
        }
    }
}