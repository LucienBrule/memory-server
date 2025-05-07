package io.brule.memory

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Path
import jakarta.ws.rs.GET
@ApplicationScoped
@Path("/hello")
class HelloEndpoint {

    @GET
    fun hello() = "Hello RESTEasy"

}