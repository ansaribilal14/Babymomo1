package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryTool @Inject constructor() : ToolExecutor {
    override val name = "memory_store"
    override val description = "Explicitly store a memory"
    override val parameters: JsonObject? = Json.parseToJsonElement(
        """{"type":"object","properties":{"content":{"type":"string"},"type":{"type":"string"}}}"""
    ).jsonObject

    override suspend fun execute(input: String): String = "Memory stored (mock)"
}