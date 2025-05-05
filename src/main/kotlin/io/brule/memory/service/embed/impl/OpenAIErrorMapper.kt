package io.brule.memory.service.embed.impl

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper

@Provider
class OpenAiErrorMapper : ResponseExceptionMapper<RuntimeException> {
    override fun toThrowable(response: Response): RuntimeException? {
        if (response.status == 429) {
            return RuntimeException("Rate limit exceeded!")
        }
        return null
    }
}