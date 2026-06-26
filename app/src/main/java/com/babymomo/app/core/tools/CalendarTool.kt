package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarTool @Inject constructor() : ToolExecutor {
    override val name = "calendar_read"
    override val description = "Read upcoming calendar events"
    override val parameters: JsonObject? = Json.parseToJsonElement(
        """{"type":"object","properties":{"days_ahead":{"type":"integer"}}}"""
    ).jsonObject

    override suspend fun execute(input: String): String = "Calendar events (mock - requires calendar permission)"
}