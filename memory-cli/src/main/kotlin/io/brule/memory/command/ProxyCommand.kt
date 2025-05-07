package io.brule.memory.command

import kotlinx.coroutines.Runnable
import picocli.CommandLine
import java.io.File
import java.lang.ProcessBuilder
import java.util.concurrent.Callable

@CommandLine.Command(name = "proxy", description = ["Start the MCP proxy server"])
class ProxyCommand : Callable<Int> {

    override fun call(): Int {
        val proxyBinary = resolveProxyBinary()

        val processBuilder = ProcessBuilder(proxyBinary)
            .redirectInput(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)

        return try {
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            exitCode
        } catch (e: Exception) {
            System.err.println("❌ Failed to start memory-proxy: ${e.message}")
            1
        }
    }

    private fun resolveProxyBinary(): String {
        val os = System.getProperty("os.name").lowercase()

        val binaryName = when {
            os.contains("win") -> {
                System.err.println("🚫 Windows detected. That's unfortunate.")
                System.err.println("🪟 If you're running this in a command prompt, please stop.")
                System.err.println("💡 This tool is built for Unix-like systems. Try WSL or better yet: dual boot.")
                throw UnsupportedOperationException("Windows is not supported. Touch grass.")
            }
            os.contains("mac") -> "memory-proxy"
            os.contains("nix") || os.contains("nux") || os.contains("bsd") -> "memory-proxy"
            else -> {
                System.err.println("🌀 Unknown OS detected: $os")
                throw UnsupportedOperationException("Unsupported OS. You may be in a mainframe.")
            }
        }

        val path = File("./$binaryName")
        if (!path.exists()) {
            throw IllegalStateException("Proxy binary not found at ${path.absolutePath}")
        }

        return path.absolutePath
    }
}