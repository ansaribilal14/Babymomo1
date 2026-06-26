package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryTool @Inject constructor() {
    val name = "memory_store"
    val description = "Explicitly store a memory"
    val parameters: JsonObject = Json.parseToJsonElement(
        """{"type":"object","properties":{"content":{"type":"string"},"type":{"type":"string"}}}"""
    ).jsonObject

    suspend fun execute(input: String): String = "Memory stored (mock)"
}