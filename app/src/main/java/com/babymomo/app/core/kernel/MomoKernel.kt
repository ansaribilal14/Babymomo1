package com.babymomo.app.core.kernel

import com.babymomo.app.core.agents.AgentOrchestrator
import com.babymomo.app.core.llm.WrappedLlmProvider
import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.core.llm.model.Tool
import com.babymomo.app.core.memory.MemoryExtractor
import com.babymomo.app.core.skills.SkillRegistry
import com.babymomo.app.core.tools.ToolRegistry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MomoKernel @Inject constructor(
    private val wrappedLlmProvider: WrappedLlmProvider,
    private val requestClassifier: RequestClassifier,
    private val toolRegistry: ToolRegistry,
    private val memoryExtractor: MemoryExtractor,
    private val skillRegistry: SkillRegistry,
    private val agentOrchestrator: AgentOrchestrator
) {

    fun streamProcess(
        userMessage: String,
        conversationHistory: List<Message>,
        messageId: String? = null
    ): Flow<LlmChunk> {
        val requestType = requestClassifier.classify(userMessage)
        val tools = toolRegistry.getAllTools()
        return when (requestType) {
            else -> {
                wrappedLlmProvider.streamChat("", conversationHistory, tools)
            }
        }
    }

    suspend fun postProcess(userMessage: String, assistantResponse: String, messageId: String?) {
        try {
            memoryExtractor.extractAndStore(userMessage, assistantResponse, messageId)
        } catch (_: Exception) {}
    }
}
