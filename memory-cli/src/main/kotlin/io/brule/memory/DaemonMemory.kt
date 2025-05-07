package io.brule.memory

import io.brule.memory.command.ConfigCommand
import io.brule.memory.command.CreateCommand
import io.brule.memory.command.ProxyCommand
import io.brule.memory.command.RecallCommand
import io.brule.memory.config.MemoryConfig
import io.brule.memory.config.MemoryConfigProducer
import io.brule.memory.util.VersionProvider
import io.quarkus.picocli.runtime.annotations.TopCommand
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import kotlinx.coroutines.Runnable
import picocli.CommandLine

@QuarkusMain
@TopCommand
@CommandLine.Command(
    name = "daemon-memory",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider::class,
    subcommands = [
        CreateCommand::class,
        RecallCommand::class,
        ProxyCommand::class,
        ConfigCommand::class
    ],
    description = ["Daemon memory cli - create, recall and manage memories"]
)
class DaemonMemory(
    private val factory: CommandLine.IFactory
): Runnable, QuarkusApplication {

    private val config: MemoryConfig by lazy{
       MemoryConfig.load()
    }

    override fun run(){
        println("Try a command like 'create' or 'recall'. Use --help for more information")
    }

    override fun run(vararg args: String?): Int {
        return CommandLine(this, factory).execute(*args)
    }
}