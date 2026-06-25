package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryTool @Inject constructor() {
    val name = "memory_store"
    val description = "Explicitly store a memory"
    val parameters: JsonObject = kotlinx.serialization.json.buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("content") { put("type", "string") }
            putJsonObject("type") { put("type", "string") }
        }
    }

    suspend fun execute(input: String): String = "Memory stored (mock)"
}
