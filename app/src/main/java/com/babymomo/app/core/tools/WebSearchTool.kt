package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSearchTool @Inject constructor() {
    val name = "web_search"
    val description = "Search the web for current information"
    val parameters: JsonObject = Json.parseToJsonElement(
        """{"type":"object","properties":{"query":{"type":"string"}},"required":["query"]}"""
    ).jsonObject

    suspend fun execute(input: String): String {
        val query = try {
            Json.parseToJsonElement(input).jsonObject["query"]?.jsonPrimitive?.content ?: input
        } catch (_: Exception) { input }
        return "Search results for: $query (mock - configure a search provider)"
    }
}