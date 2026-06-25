package com.babymomo.app.core.projects

import com.babymomo.app.data.db.dao.ProjectDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectContextProvider @Inject constructor(
    private val projectDao: ProjectDao
) {
    suspend fun getProjectContext(): String {
        val projects = projectDao.getActiveProjects()
        if (projects.isEmpty()) return ""
        return projects.joinToString("\n") { p ->
            val tasks = p.tasks?.let { parseTasks(it) } ?: emptyList()
            val taskStr = tasks.take(5).joinToString(", ") { "'$it'" }
            "- ${p.name}: ${p.description ?: "No description"}. Tasks: $taskStr"
        }
    }

    private fun parseTasks(json: String): List<String> {
        return try {
            kotlinx.serialization.json.Json.decodeFromString<List<String>>(json)
        } catch (_: Exception) { emptyList() }
    }
}
