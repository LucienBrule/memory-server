package io.brule.memory.service.embed.impl

import io.brule.memory.config.MemoryConfig
import io.brule.memory.service.embed.EmbedService
import io.quarkus.arc.profile.IfBuildProfile
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Alternative
import org.jboss.logging.Logger

@ApplicationScoped
@Alternative
@Priority(2)
@IfBuildProfile("test")
class MockEmbedServiceImpl(
    private val config: MemoryConfig
) : EmbedService {

    private val logger: Logger = Logger.getLogger(MockEmbedServiceImpl::class.java)

    override fun embed(content: String): List<Float> {
        logger.debug("MockEmbedServiceImpl generating fake embedding for: \"$content\"")
        val base = content.hashCode().toDouble()
        return List(config.mock().vectorSize()) { (0.001 * it + (base % 1_000) / 1_000_000).toFloat() }
    }
}