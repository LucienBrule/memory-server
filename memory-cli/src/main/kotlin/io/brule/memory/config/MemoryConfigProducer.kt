package io.brule.memory.config

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class MemoryConfigProducer {

    private var config: MemoryConfig = MemoryConfig.load()

    fun init(config: MemoryConfig) {
        this.config = config
    }

    @Produces
     fun produce(): MemoryConfig = config
}