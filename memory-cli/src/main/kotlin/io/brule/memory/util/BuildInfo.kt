package io.brule.memory.util

class BuildInfo {
    companion object {
        val commitSha: String? = BuildInfo::class.java.`package`.implementationVersion
        val buildTime: String? = BuildInfo::class.java.`package`.implementationTitle
    }

    override fun toString(): String {
        return "BuildInfo(commitSha=$commitSha, buildTime=$buildTime)"
    }
}