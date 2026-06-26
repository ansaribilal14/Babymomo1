package com.babymomo.app.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.core.kernel.MomoKernel
import com.babymomo.app.core.llm.model.LlmChunk
import com.babymomo.app.core.llm.model.Message
import com.babymomo.app.data.db.dao.ConversationDao
import com.babymomo.app.data.db.dao.MessageDao
import com.babymomo.app.data.db.entities.ConversationEntity
import com.babymomo.app.data.db.entities.MessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<UiMessage> = emptyList(),
    val isStreaming: Boolean = false,
    val currentConversationId: String? = null,
    val inputText: String = ""
)

data class UiMessage(
    val id: String,
    val role: String,
    val content: String,
    val routingReason: String? = null,
    val isStreaming: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application,
    private val momoKernel: MomoKernel,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var conversationHistory = mutableListOf<Message>()

    init { newConversation() }

    fun newConversation() {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            conversationDao.insert(ConversationEntity(id = id, title = "New Chat", createdAt = now, updatedAt = now))
            conversationHistory.clear()
            _uiState.update { it.copy(messages = emptyList(), currentConversationId = id) }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun send() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isStreaming) return
        viewModelScope.launch {
            val userMsgId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val userMsg = UiMessage(id = userMsgId, role = "user", content = text)
            _uiState.update { it.copy(
                messages = it.messages + userMsg,
                inputText = "",
                isStreaming = true
            )}

            conversationHistory.add(Message.user(text))

            messageDao.insert(MessageEntity(
                id = userMsgId, conversationId = _uiState.value.currentConversationId!!,
                role = "user", content = text, timestamp = now
            ))

            val assistantId = UUID.randomUUID().toString()
            val streamingMsg = UiMessage(id = assistantId, role = "assistant", content = "", isStreaming = true)
            _uiState.update { it.copy(messages = it.messages + streamingMsg) }

            val responseBuilder = StringBuilder()
            momoKernel.streamProcess(text, conversationHistory, userMsgId).collect { chunk ->
                when (chunk) {
                    is LlmChunk.Token -> {
                        responseBuilder.append(chunk.text)
                        _uiState.update { s ->
                            val msgs = s.messages.toMutableList()
                            val idx = msgs.indexOfLast { it.id == assistantId }
                            if (idx >= 0) msgs[idx] = msgs[idx].copy(content = responseBuilder.toString())
                            s.copy(messages = msgs)
                        }
                    }
                    is LlmChunk.Done -> {
                        val response = responseBuilder.toString()
                        conversationHistory.add(Message.assistant(response))
                        messageDao.insert(MessageEntity(
                            id = assistantId,
                            conversationId = _uiState.value.currentConversationId!!,
                            role = "assistant", content = response,
                            timestamp = System.currentTimeMillis(),
                            routingReason = "MomoKernel"
                        ))
                        momoKernel.postProcess(text, response, userMsgId)
                        _uiState.update { s ->
                            val msgs = s.messages.toMutableList()
                            val idx = msgs.indexOfLast { it.id == assistantId }
                            if (idx >= 0) msgs[idx] = msgs[idx].copy(isStreaming = false)
                            s.copy(messages = msgs, isStreaming = false)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
