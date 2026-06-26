package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTool @Inject constructor() : ToolExecutor {
    override val name = "send_notification"
    override val description = "Post a local Android notification"
    override val parameters: JsonObject? = Json.parseToJsonElement(
        """{"type":"object","properties":{"title":{"type":"string"},"body":{"type":"string"}}}"""
    ).jsonObject

    override suspend fun execute(input: String): String = "Notification posted (mock)"
}