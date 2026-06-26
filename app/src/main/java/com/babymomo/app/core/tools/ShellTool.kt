package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellTool @Inject constructor() : ToolExecutor {
    override val name = "shell_exec"
    override val description = "Run a shell command in the Linux sandbox"
    override val parameters: JsonObject? = Json.parseToJsonElement(
        """{"type":"object","properties":{"command":{"type":"string"}},"required":["command"]}"""
    ).jsonObject

    override suspend fun execute(input: String): String {
        val cmd = try {
            Json.parseToJsonElement(input).jsonObject["command"]?.jsonPrimitive?.content ?: input
        } catch (_: Exception) { input }
        return "Shell output for: $cmd (sandbox not installed - enable in Settings)"
    }
}