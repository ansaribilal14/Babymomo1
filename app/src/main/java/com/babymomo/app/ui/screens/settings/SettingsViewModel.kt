package com.babymomo.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.core.llm.RemoteLlmProvider
import com.babymomo.app.core.llm.WrappedLlmProvider
import com.babymomo.app.data.db.dao.SettingDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val openaiKey: String = "",
    val openaiModel: String = "gpt-4o-mini",
    val nvidiaKey: String = "",
    val nvidiaModel: String = "meta/llama3-8b-instruct",
    val openrouterKey: String = "",
    val openrouterModel: String = "openai/gpt-4o-mini",
    val soulPrompt: String = "",
    val internetOff: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingDao: SettingDao,
    private val remoteLlmProvider: RemoteLlmProvider,
    private val wrappedLlmProvider: WrappedLlmProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { loadSettings() }

    fun updateOpenaiKey(key: String) {
        _uiState.update { it.copy(openaiKey = key) }
        remoteLlmProvider.apply { if (providerLabel == "OpenAI") apiKey = key }
    }

    fun updateOpenaiModel(model: String) {
        _uiState.update { it.copy(openaiModel = model) }
    }

    fun updateNvidiaKey(key: String) {
        _uiState.update { it.copy(nvidiaKey = key) }
    }

    fun updateNvidiaModel(model: String) {
        _uiState.update { it.copy(nvidiaModel = model) }
    }

    fun updateOpenrouterKey(key: String) {
        _uiState.update { it.copy(openrouterKey = key) }
    }

    fun updateOpenrouterModel(model: String) {
        _uiState.update { it.copy(openrouterModel = model) }
    }

    fun updateSoul(soul: String) {
        _uiState.update { it.copy(soulPrompt = soul) }
        wrappedLlmProvider.soulPrompt = soul
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val soul = settingDao.get("soul_prompt")?.value ?: ""
            wrappedLlmProvider.soulPrompt = soul.ifBlank {
                "You are BabyMomo, a private AI companion that lives on the user's device."
            }
            _uiState.update { it.copy(soulPrompt = soul) }
        }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.value.let { s ->
                settingDao.insert(com.babymomo.app.data.db.entities.SettingEntity("soul_prompt", s.soulPrompt))
                settingDao.insert(com.babymomo.app.data.db.entities.SettingEntity("openai_key", s.openaiKey))
                settingDao.insert(com.babymomo.app.data.db.entities.SettingEntity("nvidia_key", s.nvidiaKey))
                settingDao.insert(com.babymomo.app.data.db.entities.SettingEntity("openrouter_key", s.openrouterKey))
            }
        }
    }
}
