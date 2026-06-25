package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarTool @Inject constructor() {
    val name = "calendar_read"
    val description = "Read upcoming calendar events"
    val parameters: JsonObject = kotlinx.serialization.json.buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("days_ahead") { put("type", "integer") }
        }
    }

    suspend fun execute(input: String): String = "Calendar events (mock - requires calendar permission)"
}
