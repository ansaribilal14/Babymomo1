package com.babymomo.app.ui.screens.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.core.memory.MemoryService
import com.babymomo.app.data.db.dao.EntityDao
import com.babymomo.app.data.db.dao.RelationDao
import com.babymomo.app.data.db.entities.MemoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoryUiState(
    val memories: List<MemoryEntity> = emptyList(),
    val selectedType: String = "ALL",
    val searchQuery: String = "",
    val activeCount: Int = 0,
    val totalCount: Int = 0,
    val entityCount: Int = 0,
    val relationCount: Int = 0,
    val promotedCount: Int = 0
)

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val memoryService: MemoryService,
    private val entityDao: EntityDao,
    private val relationDao: RelationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryUiState())
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    init { loadStats() }

    fun selectType(type: String) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) return
        viewModelScope.launch {
            val results = memoryService.searchMemories(query)
            _uiState.update { it.copy(memories = results) }
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            memoryService.deleteMemory(memory)
            loadStats()
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    activeCount = memoryService.getActiveCount(),
                    totalCount = memoryService.getTotalCount(),
                    entityCount = entityDao.getCount(),
                    relationCount = relationDao.getCount(),
                    promotedCount = memoryService.getPromotedCount()
                )
            }
        }
    }
}
