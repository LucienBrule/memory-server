package io.brule.memory.http

import io.brule.memory.dto.MemoryDto
import io.brule.memory.dto.MemoryMetadataDto
import io.brule.memory.dto.MemoryRecallRequest
import io.brule.memory.dto.MemoryRecallResponse
import io.brule.memory.dto.MemoryRememberRequest
import io.brule.memory.dto.MemoryRememberResponse
import io.brule.memory.dto.MemoryTagDto
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.ws.rs.core.MediaType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@QuarkusTest
class DevEndpointTest {

    @Test
    fun `remember then recall memory`() {
        val memory = MemoryDto(
            content = "The quick brown fox jumps over the lazy dog",
            metadata = MemoryMetadataDto(
                tags = listOf(MemoryTagDto("animal", "fox")),
                summary = "A test sentence for embeddings",
                uid = "test-uid-fox",
                createdAt = "2025-05-04T23:45:00Z"
            )
        )

        val rememberRequest = MemoryRememberRequest(memory)

        // Store memory
        val rememberResponse = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(rememberRequest)
            .post("/api/dev/remember")
            .then()
            .statusCode(200)
            .extract()
            .`as`(MemoryRememberResponse::class.java)

        Assertions.assertTrue(rememberResponse.acknowledged)

        // Recall memory
        val recallRequest = MemoryRecallRequest(
            memory = MemoryDto(content = "quick brown fox"),
            page = 1,
            pageSize = 3
        )

        val recallResponse = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(recallRequest)
            .post("/api/dev/recall")
            .then()
            .statusCode(200)
            .extract()
            .`as`(MemoryRecallResponse::class.java)

        Assertions.assertTrue(recallResponse.memories.any { it.metadata.uid == "test-uid-fox" })
    }
}