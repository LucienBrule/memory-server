package io.brule.memory.config

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.nio.file.Files
import java.nio.file.Path

/**
 * Configuration for the Memory CLI, loaded from TOML and overridden by environment variables.
 */
@Serializable
data class MemoryConfig(
    /** The base URL of the memory-server REST endpoint. */
    val endpoint: String,

    /** The API key used for authenticating CLI requests. */
    val apiKey: String,

    /** Active profile, e.g. "prod", "staging", or "dev". */
    val profile: String = "default"
) {
    companion object {
        /**
         * Loads configuration from the given TOML file path, then applies environment-variable
         * overrides for endpoint, apiKey, and profile if present.
         */
        fun load(path: Path = defaultPath()): MemoryConfig {
            // Read and parse the TOML file
            val raw = Files.readString(path)
            val fileConfig = Toml.decodeFromString<MemoryConfig>(raw)

            // Override with environment variables if set
            val endpoint = System.getenv("MEMORY_ENDPOINT") ?: fileConfig.endpoint
            val apiKey   = System.getenv("MEMORY_API_KEY")   ?: fileConfig.apiKey
            val profile  = System.getenv("MEMORY_PROFILE")   ?: fileConfig.profile

            return MemoryConfig(endpoint, apiKey, profile)
        }

        /**
         * Default config file path: $HOME/.memory/config.toml
         */
        fun defaultPath(): Path =
            Path.of(System.getProperty("user.home"), ".memory", "config.toml")
    }
}