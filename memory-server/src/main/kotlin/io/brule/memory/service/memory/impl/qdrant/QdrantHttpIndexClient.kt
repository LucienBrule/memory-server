package io.brule.memory.service.memory.impl.qdrant

import io.brule.memory.config.MemoryConfig
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@ApplicationScoped
class QdrantHttpIndexClient(
  private val config: MemoryConfig,
){

  private val client: HttpClient = HttpClient.newHttpClient()

  private val baseUrl = "${if (config.qdrant().secure()) "https" else "http"}://${config.qdrant().host()}:${config.qdrant().port()}/collections/${config.qdrant().collectionName()}"
  fun ensureIndex(field: String) {
    val url = "${baseUrl}/payload/index"
    val body = """
      {
        "field_name": "$field",
        "field_schema": "keyword"
      }
    """.trimIndent()

    val request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .header("Content-Type", "application/json")
      .header("api-key", config.qdrant().apiKey())
      .PUT(HttpRequest.BodyPublishers.ofString(body))
      .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() !in 200..299) {
      throw RuntimeException("Failed to ensure index for field '$field': ${response.statusCode()} - ${response.body()}")
    }
  }

  /**
   * Ensure indexing for a list of fields.
   */
  fun ensureIndexes(fields: List<String>) {
    for (field in fields) {
      try {
        ensureIndex(field)
      } catch (e: Exception) {
        println("Warning: Failed to ensure index for field '$field': ${e.message}")
      }
    }
  }

  fun ensureAllIndexes() {
    ensureIndexes(listOf("topic", "qa", "env", "session"))
  }
}