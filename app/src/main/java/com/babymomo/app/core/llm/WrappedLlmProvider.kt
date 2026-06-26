package com.babymomo.app.core.llm

import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.core.llm.model.Tool
import com.babymomo.app.core.memory.MemoryService
import com.babymomo.app.core.projects.ProjectContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.emitAll
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WrappedLlmProvider @Inject constructor(
    private val llmChain: LlmChain,
    private val memoryService: MemoryService,
    private val projectContextProvider: ProjectContextProvider
) : LlmProvider {

    var soulPrompt: String = "You are BabyMomo, a private AI companion that lives on the user's device. " +
        "You remember everything important, grow smarter over time, and keep all data private."

    override fun streamChat(
        systemPrompt: String,
        messages: List<Message>,
        tools: List<Tool>
    ): Flow<LlmChunk> = flow {
        val enrichedSystemPrompt = buildSystemPrompt(systemPrompt, messages)
        emitAll(llmChain.streamChat(enrichedSystemPrompt, messages, tools))
    }.flowOn(Dispatchers.IO)

    override suspend fun complete(prompt: String): String {
        return llmChain.complete(prompt)
    }

    override fun isAvailable(): Boolean = llmChain.isAvailable()
    override fun providerName(): String = "Wrapped(${llmChain.providerName()})"

    private suspend fun buildSystemPrompt(systemPrompt: String, messages: List<Message>): String {
        val sb = StringBuilder()

        sb.append("[CORE SOUL]\n").append(soulPrompt).append("\n\n")

        val promotedMemories = memoryService.getPromotedMemories()
        if (promotedMemories.isNotEmpty()) {
            sb.append("[PROMOTED MEMORIES - PERMANENT]\n")
            promotedMemories.forEach { sb.append("- ${it.content}\n") }
            sb.append("\n")
        }

        val query = messages.lastOrNull { it.role == "user" }?.content ?: ""
        val recalled = memoryService.recallMemories(query, topK = 8)
        if (recalled.isNotEmpty()) {
            sb.append("[RECALLED MEMORIES - THIS TURN]\n")
            recalled.forEach {
                sb.append("- [${it.memory.id}] ${it.memory.content}\n")
            }
            sb.append("\n")
        }

        val projectContext = projectContextProvider.getProjectContext()
        if (projectContext.isNotBlank()) {
            sb.append("[ACTIVE PROJECTS]\n").append(projectContext).append("\n")
        }

        val now = Date()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        sb.append("[CONTEXT]\n")
        sb.append("Current date: ${dateFormat.format(now)}\n")
        sb.append("Current time: ${timeFormat.format(now)}\n")

        if (systemPrompt.isNotBlank()) {
            sb.append("\n[ADDITIONAL INSTRUCTIONS]\n").append(systemPrompt).append("\n")
        }

        return sb.toString()
    }
}