package com.babymomo.app.core.tools

import com.babymomo.app.core.llm.model.Tool
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    private val webSearchTool: WebSearchTool,
    private val notificationTool: NotificationTool,
    private val calendarTool: CalendarTool,
    private val shellTool: ShellTool,
    private val memoryTool: MemoryTool
) {
    private val toolList = listOf(webSearchTool, notificationTool, calendarTool, shellTool, memoryTool)

    fun getAllTools(): List<Tool> = toolList.map { Tool(it.name, it.description, it.parameters) }

    suspend fun execute(name: String, input: String): String {
        return toolList.find { it.name == name }?.execute(input) ?: "Tool not found: $name"
    }
}