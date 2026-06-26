package com.babymomo.app.core.llm.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Tool(
    val name: String,
    val description: String,
    val parameters: JsonObject? = null
)

@Serializable
data class ToolCall(
    val id: String,
    val name: String,
    val arguments: String
)

@Serializable
data class ToolResult(
    val callId: String,
    val result: String
)