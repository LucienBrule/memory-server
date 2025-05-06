package io.brule.memory.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "memory")
interface MemoryConfig {

    fun httpServer(): HttpServer
    interface HttpServer {
        fun host(): String
        fun port(): Int
    }

    fun grpcServer(): GrpcServer
    interface GrpcServer {
        fun host(): String
        fun port(): Int
    }

    fun qdrant(): Qdrant
    interface Qdrant {
        fun host(): String
        fun port(): Int
        fun apiKey(): String

        fun vector(): Vector
        interface Vector {
            fun name(): String
            fun size(): Long
        }

        fun collectionName(): String
    }

    fun openai(): OpenAi
    interface OpenAi {
        fun apiKey(): String

        fun embedding(): Embedding
        interface Embedding {
            fun model(): String
            fun encodingFormat(): String
        }

        fun completion(): Completion
        interface Completion {
            fun model(): String
        }
    }

    fun mock(): Mock
    interface Mock {
        fun vectorSize(): Int
    }
}
