package com.babymomo.app.core.tools

import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTool @Inject constructor() {
    val name = "send_notification"
    val description = "Post a local Android notification"
    val parameters: JsonObject = kotlinx.serialization.json.buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("title") { put("type", "string") }
            putJsonObject("body") { put("type", "string") }
        }
    }

    suspend fun execute(input: String): String = "Notification posted (mock)"
}
