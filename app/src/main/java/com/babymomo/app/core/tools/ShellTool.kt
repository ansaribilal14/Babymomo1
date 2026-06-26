package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellTool @Inject constructor() {
    val name = "shell_exec"
    val description = "Run a shell command in the Linux sandbox"
    val parameters: JsonObject = kotlinx.serialization.json.buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("command") { put("type", "string") }
        }
        put("required", kotlinx.serialization.json.buildJsonArray { add("command") })
    }

    suspend fun execute(input: String): String {
        val cmd = try {
            kotlinx.serialization.json.Json.parseToJsonElement(input).jsonObject["command"]?.jsonPrimitive?.content ?: input
        } catch (_: Exception) { input }
        return "Shell output for: $cmd (sandbox not installed - enable in Settings)"
    }
}
