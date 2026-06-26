package com.babymomo.app.core.mcp

import com.babymomo.app.core.llm.model.Tool
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class McpTool @Inject constructor() {
    fun toTool(name: String, description: String): Tool = Tool(name, description, null)
}