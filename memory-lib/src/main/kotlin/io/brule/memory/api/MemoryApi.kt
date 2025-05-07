package io.brule.memory.api

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

/**
 * Transport‑agnostic contract for remembering and recalling memories.
 *
 * This interface is annotated with JAX‑RS so that:
 *  • **memory‑server** can implement it directly as a REST endpoint.
 *  • **memory‑cli** can generate a REST client or swap in other adapters (gRPC, MCP, etc.)
 *
 * Future transports (gRPC, MCP) can reuse this interface without relying on
 * the JAX‑RS annotations—each adapter just ignores what it doesn’t need.
 */
@Path("/api/memory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface MemoryApi {

    /** Store a new memory. */
    @POST
    @Path("/remember")
    fun remember(request: MemoryRememberRequest): MemoryRememberResponse

    /** Recall memories by semantic content and/or tags. */
    @POST
    @Path("/recall")
    fun recall(request: MemoryRecallRequest): MemoryRecallResponse
}