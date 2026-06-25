package com.babymomo.app.core.projects

import com.babymomo.app.data.db.dao.ProjectDao
import com.babymomo.app.data.db.entities.ProjectEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectService @Inject constructor(
    private val projectDao: ProjectDao
) {
    private val json = Json

    suspend fun createProject(name: String, description: String, tasks: List<String> = emptyList()): ProjectEntity {
        val now = System.currentTimeMillis()
        val project = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = name, description = description,
            tasks = json.encodeToString(tasks),
            createdAt = now, updatedAt = now
        )
        projectDao.insert(project)
        return project
    }

    suspend fun updateTasks(projectId: String, tasks: List<String>) {
        val project = projectDao.getProject(projectId) ?: return
        val updated = project.copy(
            tasks = json.encodeToString(tasks),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.update(updated)
    }
}
