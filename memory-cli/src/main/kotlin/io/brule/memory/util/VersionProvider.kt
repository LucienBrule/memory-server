package io.brule.memory.util

import picocli.CommandLine

class VersionProvider : CommandLine.IVersionProvider {
    override fun getVersion(): Array<String> =
        arrayOf("daemon-memory 0.0.1", "commit: ${BuildInfo.commitSha}", "built: ${BuildInfo.buildTime}")
}