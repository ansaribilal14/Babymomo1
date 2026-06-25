package com.babymomo.app.core.llm.model

sealed class LlmChunk {
    data class Token(val text: String) : LlmChunk()
    data class ToolCall(val id: String, val name: String, val input: String) : LlmChunk()
    data class ToolResult(val callId: String, val result: String) : LlmChunk()
    object Done : LlmChunk()
    data class Error(val message: String) : LlmChunk()
}
