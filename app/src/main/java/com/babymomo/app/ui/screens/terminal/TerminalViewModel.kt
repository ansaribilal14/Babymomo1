package com.babymomo.app.ui.screens.terminal
import androidx.lifecycle.ViewModel
import com.babymomo.app.core.sandbox.SandboxSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val sandboxSession: SandboxSession
) : ViewModel() {
    private val _output = MutableStateFlow<List<String>>(emptyList())
    val output: StateFlow<List<String>> = _output.asStateFlow()
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    fun updateInput(s: String) { _input.update { s } }
    fun execute() {
        val cmd = _input.value.trim()
        if (cmd.isBlank()) return
        val result = sandboxSession.execute(cmd)
        _output.update { it + "$ $cmd" + result }
        _input.update { "" }
    }
    fun clear() { _output.update { emptyList() }; sandboxSession.clear() }
}
