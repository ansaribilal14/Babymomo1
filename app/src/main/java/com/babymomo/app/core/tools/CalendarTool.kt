package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarTool @Inject constructor() {
    val name = "calendar_read"
    val description = "Read upcoming calendar events"
    val parameters: JsonObject = Json.parseToJsonElement(
        """{"type":"object","properties":{"days_ahead":{"type":"integer"}}}"""
    ).jsonObject

    suspend fun execute(input: String): String = "Calendar events (mock - requires calendar permission)"
}