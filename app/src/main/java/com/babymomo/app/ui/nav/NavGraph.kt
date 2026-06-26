package com.babymomo.app.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.babymomo.app.ui.screens.chat.ChatScreen
import com.babymomo.app.ui.screens.heartbeat.HeartbeatScreen
import com.babymomo.app.ui.screens.interactive.InteractiveScreen
import com.babymomo.app.ui.screens.memory.MemoryScreen
import com.babymomo.app.ui.screens.models.ModelsScreen
import com.babymomo.app.ui.screens.mcp.McpScreen
import com.babymomo.app.ui.screens.projects.ProjectsScreen
import com.babymomo.app.ui.screens.settings.SettingsScreen
import com.babymomo.app.ui.screens.skills.SkillsScreen
import com.babymomo.app.ui.screens.terminal.TerminalScreen

@Composable
fun BabyMomoNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Route.Chat.route) {
        composable(Route.Chat.route) { ChatScreen(navController = navController) }
        composable(Route.Memory.route) { MemoryScreen() }
        composable(Route.Projects.route) { ProjectsScreen() }
        composable(Route.Models.route) { ModelsScreen() }
        composable(Route.Settings.route) { SettingsScreen(navController = navController) }
        composable(Route.Skills.route) { SkillsScreen() }
        composable(Route.Heartbeat.route) { HeartbeatScreen() }
        composable(Route.Terminal.route) { TerminalScreen() }
        composable(Route.McpServers.route) { McpScreen() }
        composable(Route.Interactive.route) { backStackEntry ->
            val descriptor = backStackEntry.arguments?.getString("descriptor") ?: ""
            InteractiveScreen(descriptorJson = descriptor, navController = navController)
        }
    }
}