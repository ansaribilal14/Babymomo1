package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellTool @Inject constructor() {
    val name = "shell_exec"
    val description = "Run a shell command in the Linux sandbox"
    val parameters: JsonObject = Json.parseToJsonElement(
        """{"type":"object","properties":{"command":{"type":"string"}},"required":["command"]}"""
    ).jsonObject

    suspend fun execute(input: String): String {
        val cmd = try {
            Json.parseToJsonElement(input).jsonObject["command"]?.jsonPrimitive?.content ?: input
        } catch (_: Exception) { input }
        return "Shell output for: $cmd (sandbox not installed - enable in Settings)"
    }
}