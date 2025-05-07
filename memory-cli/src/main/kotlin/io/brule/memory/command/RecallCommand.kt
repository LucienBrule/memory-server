package io.brule.memory.command

import io.brule.memory.api.MemoryApi
import io.brule.memory.api.MemoryDto
import io.brule.memory.api.MemoryRecallRequest
import picocli.CommandLine

@CommandLine.Command(name = "recall", description = ["Recall memories by content"])
class RecallCommand(
    private val memoryClient: MemoryApi
) : Runnable {

    @CommandLine.Option(names = ["--query"], required = true)
    lateinit var query: String

    override fun run() {
        val request = MemoryRecallRequest(
            memory = MemoryDto(content = query)
        )
        val response = memoryClient.recall(request)
        response.memories.forEachIndexed { idx, mem ->
            println("${idx + 1}. ${mem.content} [${mem.metadata.tags.joinToString { "${it.name}=${it.value}" }}]")
        }
    }
}