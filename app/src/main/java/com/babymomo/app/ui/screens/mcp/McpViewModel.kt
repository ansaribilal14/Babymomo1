package com.babymomo.app.ui.screens.mcp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.core.mcp.McpServerRegistry
import com.babymomo.app.data.db.entities.McpServerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class McpViewModel @Inject constructor(
    private val mcpServerRegistry: McpServerRegistry
) : ViewModel() {
    fun getCuratedServers() = mcpServerRegistry.getCuratedServers()
    fun addServer(name: String, url: String) {
        viewModelScope.launch { mcpServerRegistry.addServer(name, url) }
    }
}