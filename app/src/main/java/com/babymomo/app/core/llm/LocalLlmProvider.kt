package com.babymomo.app.core.llm

import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.core.llm.model.Tool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLlmProvider @Inject constructor() : LlmProvider {

    var isActive: Boolean = false
    var modelName: String = ""

    override fun providerName(): String = "Local ($modelName)"

    override fun isAvailable(): Boolean = isActive

    override fun streamChat(
        systemPrompt: String,
        messages: List<Message>,
        tools: List<Tool>
    ): Flow<LlmChunk> = flow {
        emit(LlmChunk.Token("On-device inference is available when a LiteRT model is downloaded and activated. "))
        emit(LlmChunk.Token("Go to the Models tab to download Gemma 2B or Phi-3 Mini. "))
        emit(LlmChunk.Done)
    }

    override suspend fun complete(prompt: String): String =
        "Local LLM placeholder. Download a model to enable on-device inference."
}
