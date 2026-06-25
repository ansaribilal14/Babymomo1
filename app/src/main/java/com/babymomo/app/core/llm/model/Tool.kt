package com.babymomo.app.core.llm.model

import kotlinx.serialization.Serializable

@Serializable
data class Tool(
    val name: String,
    val description: String,
    val parameters: Map<String, Any> = emptyMap()
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
