package io.brule.memory.command

import io.brule.memory.api.MemoryApi
import io.brule.memory.api.MemoryDto
import io.brule.memory.api.MemoryMetadataDto
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryTagDto
import jakarta.inject.Inject
import picocli.CommandLine

@CommandLine.Command(name = "create", description = ["Create and store a new memory"])
class CreateCommand(
    private val memoryClient: MemoryApi
) : Runnable {

    @CommandLine.Option(names = ["--content"], required = true)
    lateinit var content: String

    @CommandLine.Option(names = ["--tag"], description = ["tag in name=value format"], split = ",")
    var tags: List<String> = emptyList()

    override fun run() {
        val dto = MemoryRememberRequest(
            memory = MemoryDto(
                content = content,
                metadata = MemoryMetadataDto(
                    tags = tags.map {
                        val (name, value) = it.split("=")
                        MemoryTagDto(name, value)
                    }
                )
            )
        )
        val response = memoryClient.remember(dto)
        println("✅ Acknowledged: ${response.acknowledged}")
    }
}