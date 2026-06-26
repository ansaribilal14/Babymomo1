package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTool @Inject constructor() {
    val name = "send_notification"
    val description = "Post a local Android notification"
    val parameters: JsonObject = Json.parseToJsonElement(
        """{"type":"object","properties":{"title":{"type":"string"},"body":{"type":"string"}}}"""
    ).jsonObject

    suspend fun execute(input: String): String = "Notification posted (mock)"
}