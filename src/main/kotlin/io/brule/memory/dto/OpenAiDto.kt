package io.brule.memory.dto
import com.fasterxml.jackson.annotation.JsonProperty

data class OpenAiEmbeddingRequest(
    @JsonProperty("model") val model: String,
    @JsonProperty("input") val input: String,
    @JsonProperty("encoding_format") val encodingFormat: String = "float"
)

data class OpenAiEmbeddingResponse(
    val data: List<EmbeddingWrapper>
)

data class EmbeddingWrapper(
    val embedding: List<Double>
)