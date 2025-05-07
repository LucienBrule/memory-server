package io.brule.memory.command

import io.brule.memory.config.MemoryConfig
import kotlinx.coroutines.Runnable
import picocli.CommandLine

@CommandLine.Command(name = "config", description = ["Configure the CLI"])
class ConfigCommand(
    private val config: MemoryConfig
) : Runnable{
    override fun run() {
        println("""
            endpoint: ${config.endpoint}
            profile: ${config.profile}
            apiKey: ${config.apiKey} 
        """.trimIndent())
    }
}