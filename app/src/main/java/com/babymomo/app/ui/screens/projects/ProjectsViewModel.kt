package com.babymomo.app.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymomo.app.core.projects.ProjectService
import com.babymomo.app.data.db.entities.ProjectEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val projects: List<ProjectEntity> = emptyList(),
    val showCreateDialog: Boolean = false,
    val newProjectName: String = "",
    val newProjectDesc: String = ""
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectService: ProjectService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    fun showCreate() { _uiState.update { it.copy(showCreateDialog = true) } }
    fun hideCreate() { _uiState.update { it.copy(showCreateDialog = false, newProjectName = "", newProjectDesc = "") } }
    fun updateName(n: String) { _uiState.update { it.copy(newProjectName = n) } }
    fun updateDesc(d: String) { _uiState.update { it.copy(newProjectDesc = d) } }

    fun createProject() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.newProjectName.isNotBlank()) {
                projectService.createProject(s.newProjectName, s.newProjectDesc)
                hideCreate()
            }
        }
    }
}
