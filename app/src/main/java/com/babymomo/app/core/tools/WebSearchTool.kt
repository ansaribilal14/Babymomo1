package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSearchTool @Inject constructor() {
    val name = "web_search"
    val description = "Search the web for current information"
    val parameters: JsonObject = kotlinx.serialization.json.buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("query") { put("type", "string") }
        }
        put("required", kotlinx.serialization.json.buildJsonArray { add("query") })
    }

    suspend fun execute(input: String): String {
        val query = try {
            kotlinx.serialization.json.Json.parseToJsonElement(input).jsonObject["query"]?.jsonPrimitive?.content ?: input
        } catch (_: Exception) { input }
        return "Search results for: $query (mock - configure a search provider)"
    }
}
