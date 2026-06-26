package com.babymomo.app.ui.nav

sealed class Route(val route: String) {
    data object Chat : Route("chat")
    data object Memory : Route("memory")
    data object Projects : Route("projects")
    data object Models : Route("models")
    data object Settings : Route("settings")
    data object Skills : Route("skills")
    data object Heartbeat : Route("heartbeat")
    data object Terminal : Route("terminal")
    data object McpServers : Route("mcp_servers")
    data object Interactive : Route("interactive/{descriptor}") {
        fun create(descriptor: String) = "interactive/$descriptor"
    }
}
