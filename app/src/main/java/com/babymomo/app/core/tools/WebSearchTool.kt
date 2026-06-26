package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSearchTool @Inject constructor() : ToolExecutor {
    override val name = "web_search"
    override val description = "Search the web for current information"
    override val parameters: JsonObject? = Json.parseToJsonElement(
        """{"type":"object","properties":{"query":{"type":"string"}},"required":["query"]}"""
    ).jsonObject

    suspend fun execute(input: String): String {
        val query = try {
            Json.parseToJsonElement(input).jsonObject["query"]?.jsonPrimitive?.content ?: input
        } catch (_: Exception) { input }
        return "Search results for: $query (mock - configure a search provider)"
    }
}