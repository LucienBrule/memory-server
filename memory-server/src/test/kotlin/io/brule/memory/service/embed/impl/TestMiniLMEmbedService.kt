package io.brule.memory.service.embed.impl

import io.brule.memory.service.embed.EmbedService
import io.quarkus.arc.profile.IfBuildProfile
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Alternative
import org.jboss.logging.Logger
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel


@ApplicationScoped
@Alternative
@Priority(1)
@IfBuildProfile("test-minilm")
class TestMiniLMEmbedService: EmbedService {

    private val logger: Logger = Logger.getLogger(TestMiniLMEmbedService::class.java)
    private val model = AllMiniLmL6V2QuantizedEmbeddingModel()
    override fun embed(content: String): List<Double> {
        logger.debug("TestMiniLMEmbedService generating embedding for: \"$content\"")
        val embeddingResult = model.embed(content)
        // Extract the raw float array from the model's output
        val floatVector = embeddingResult.content().vector()
        // Convert float values to Double
        return floatVector.map { it.toDouble() }
    }
}