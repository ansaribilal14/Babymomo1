package com.babymomo.app.core.mcp

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class McpClient @Inject constructor() {
    suspend fun connect(url: String): Boolean = false
    suspend fun listTools(serverUrl: String): List<String> = emptyList()
    suspend fun callTool(serverUrl: String, toolName: String, input: String): String = "MCP tool call (mock)"
}
