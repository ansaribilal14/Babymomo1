package com.babymomo.app.core.llm

import android.util.Log
import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.core.llm.model.Tool
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteLlmProvider @Inject constructor() : LlmProvider {

    private val client = HttpClient(OkHttp) {
        expectSuccess = false
    }

    var apiKey: String = ""
    var baseUrl: String = ""
    var model: String = ""
    var providerLabel: String = "Remote"

    override fun providerName(): String = providerLabel

    override fun isAvailable(): Boolean =
        apiKey.isNotBlank() && baseUrl.isNotBlank() && model.isNotBlank()

    override fun streamChat(
        systemPrompt: String,
        messages: List<Message>,
        tools: List<Tool>
    ): Flow<LlmChunk> = flow {
        if (!isAvailable()) {
            emit(LlmChunk.Error("Remote provider not configured"))
            emit(LlmChunk.Done)
            return@flow
        }

        val allMessages = buildJsonArray {
            addJsonObject { put("role", "system"); put("content", systemPrompt) }
            for (msg in messages) {
                addJsonObject {
                    put("role", msg.role)
                    put("content", msg.content)
                }
            }
        }

        val requestJson = buildJsonObject {
            put("model", model)
            put("messages", allMessages)
            put("stream", true)
            if (tools.isNotEmpty()) {
                val toolsArray = buildJsonArray {
                    for (tool in tools) {
                        addJsonObject {
                            put("type", "function")
                            putJsonObject("function") {
                                put("name", tool.name)
                                put("description", tool.description)
                                put("parameters", Json.encodeToJsonElement(tool.parameters))
                            }
                        }
                    }
                }
                put("tools", toolsArray)
            }
        }

        try {
            val response = client.post("$baseUrl/v1/chat/completions") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                setBody(requestJson.toString())
            }

            if (response.status != HttpStatusCode.OK) {
                val body = response.bodyAsText()
                emit(LlmChunk.Error("HTTP ${response.status}: $body"))
                emit(LlmChunk.Done)
                return@flow
            }

            val channel = response.bodyAsChannel()
            val buffer = StringBuilder()
            while (!channel.isClosedForRead && isActive) {
                val packet = channel.readRemaining(4096)
                val text = packet.readText()
                buffer.append(text)
                val lines = buffer.toString().split("\n")
                buffer.clear()
                buffer.append(lines.lastOrNull() ?: "")
                for (line in lines.dropLast(1)) {
                    val trimmed = line.trim()
                    if (trimmed.startsWith("data: ")) {
                        val data = trimmed.removePrefix("data: ")
                        if (data == "[DONE]") {
                            emit(LlmChunk.Done)
                            return@flow
                        }
                        try {
                            val json = Json.parseToJsonElement(data).jsonObject
                            val delta = json["choices"]?.jsonArray?.firstOrNull()
                                ?.jsonObject?.get("delta")?.jsonObject
                            val content = delta?.get("content")?.jsonPrimitive?.content
                            if (!content.isNullOrEmpty()) {
                                emit(LlmChunk.Token(content))
                            }
                            val toolCalls = delta?.get("tool_calls")?.jsonArray
                            if (toolCalls != null) {
                                for (tc in toolCalls) {
                                    val tcObj = tc.jsonObject
                                    val fn = tcObj["function"]?.jsonObject
                                    val id = tcObj["id"]?.jsonPrimitive?.content ?: ""
                                    val name = fn?.get("name")?.jsonPrimitive?.content ?: ""
                                    val args = fn?.get("arguments")?.jsonPrimitive?.content ?: "{}"
                                    emit(LlmChunk.ToolCall(id, name, args))
                                }
                            }
                        } catch (e: Exception) {
                            Log.w("RemoteLlm", "Parse error: ${e.message}")
                        }
                    }
                }
            }
            emit(LlmChunk.Done)
        } catch (e: Exception) {
            emit(LlmChunk.Error("Connection error: ${e.message}"))
            emit(LlmChunk.Done)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun complete(prompt: String): String {
        val chunks = mutableListOf<String>()
        streamChat("", listOf(Message.user(prompt))).collect { chunk ->
            when (chunk) {
                is LlmChunk.Token -> chunks.add(chunk.text)
                is LlmChunk.Done -> {}
                else -> {}
            }
        }
        return chunks.joinToString("")
    }
}
