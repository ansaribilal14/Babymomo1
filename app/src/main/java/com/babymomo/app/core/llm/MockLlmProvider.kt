package com.babymomo.app.core.llm

import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.core.llm.model.Tool
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockLlmProvider @Inject constructor() : LlmProvider {

    override fun streamChat(
        systemPrompt: String,
        messages: List<Message>,
        tools: List<Tool>
    ): Flow<LlmChunk> = flow {
        val lastUserMsg = messages.lastOrNull { it.role == "user" }?.content ?: ""
        val response = generateMockResponse(lastUserMsg)
        val words = response.split(" ")
        for (word in words) {
            emit(LlmChunk.Token("$word "))
            delay(30)
        }
        emit(LlmChunk.Done)
    }

    override suspend fun complete(prompt: String): String = generateMockResponse(prompt)

    override fun isAvailable(): Boolean = true
    override fun providerName(): String = "Mock"

    private fun generateMockResponse(input: String): String {
        val lower = input.lowercase(Locale.getDefault())
        return when {
            lower.contains("hello") || lower.contains("hi") ->
                "Hello! I'm BabyMomo, your personal AI companion. I'm running in offline mock mode right now. " +
                "Configure an API key in Settings to connect me to a real AI model, or download an on-device model. " +
                "How can I help you today?"
            lower.contains("who are you") ->
                "I'm BabyMomo, a private AI companion that lives on your device. I remember everything important " +
                "about you, grow smarter over time, and can execute real tools like web search and calendar. " +
                "I'm currently running in mock mode for development."
            lower.contains("memory") ->
                "I have a bi-temporal memory system with four types: Working (short-term), Episodic (events), " +
                "Semantic (facts about you), and Procedural (how you prefer things). Memories that get used " +
                "frequently get promoted to permanent context so I never forget what matters."
            lower.isEmpty() -> "I'm listening! What would you like to talk about?"
            else -> "That's interesting! In mock mode, I generate canned responses. " +
                "Connect a real AI provider (OpenAI, NVIDIA NIM, or OpenRouter) in Settings, " +
                "or download Gemma 2B / Phi-3 Mini for on-device intelligence. " +
                "I understood you said: \"${input.take(80)}\""
        }
    }
}
