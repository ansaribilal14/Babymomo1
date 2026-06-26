package com.babymomo.app.core.llm

import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.core.llm.model.Tool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmChain @Inject constructor(
    private val localLlmProvider: LocalLlmProvider,
    private val remoteLlmProvider: RemoteLlmProvider,
    private val mockLlmProvider: MockLlmProvider
) : LlmProvider {

    private val providers: List<LlmProvider>
        get() = listOf(localLlmProvider, remoteLlmProvider, mockLlmProvider)

    override fun streamChat(
        systemPrompt: String,
        messages: List<Message>,
        tools: List<Tool>
    ): Flow<LlmChunk> {
        for (provider in providers) {
            if (provider.isAvailable()) {
                return provider.streamChat(systemPrompt, messages, tools)
            }
        }
        return flowOf(
            LlmChunk.Error("No LLM provider available"),
            LlmChunk.Done
        )
    }

    override suspend fun complete(prompt: String): String {
        for (provider in providers) {
            if (provider.isAvailable()) {
                return provider.complete(prompt)
            }
        }
        return "No LLM provider available"
    }

    override fun isAvailable(): Boolean = true
    override fun providerName(): String = "LlmChain"
}
