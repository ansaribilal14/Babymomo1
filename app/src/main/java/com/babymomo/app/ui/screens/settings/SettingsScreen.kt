package com.babymomo.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babymomo.app.ui.nav.Route
import com.babymomo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController? = null, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold, color = DarkOnBg) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Providers
            SectionHeader("AI Providers")

            ProviderCard("OpenAI", uiState.openaiKey, uiState.openaiModel,
                onKeyChange = { viewModel.updateOpenaiKey(it) },
                onModelChange = { viewModel.updateOpenaiModel(it) })

            ProviderCard("NVIDIA NIM", uiState.nvidiaKey, uiState.nvidiaModel,
                onKeyChange = { viewModel.updateNvidiaKey(it) },
                onModelChange = { viewModel.updateNvidiaModel(it) })

            ProviderCard("OpenRouter", uiState.openrouterKey, uiState.openrouterModel,
                onKeyChange = { viewModel.updateOpenrouterKey(it) },
                onModelChange = { viewModel.updateOpenrouterModel(it) })

            HorizontalDivider(color = DarkOutline)

            // Soul
            SectionHeader("Soul (System Prompt)")
            OutlinedTextField(
                value = uiState.soulPrompt,
                onValueChange = { viewModel.updateSoul(it) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Define BabyMomo's personality...") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = DarkOutline, textColor = DarkOnBg
                )
            )

            HorizontalDivider(color = DarkOutline)

            // Tools navigation
            SectionHeader("Tools")
            SettingsNavItem("MCP Servers") { navController?.navigate(Route.McpServers.route) }
            SettingsNavItem("Terminal") { navController?.navigate(Route.Terminal.route) }
            SettingsNavItem("Heartbeat Log") { navController?.navigate(Route.Heartbeat.route) }
            SettingsNavItem("Skills") { navController?.navigate(Route.Skills.route) }

            HorizontalDivider(color = DarkOutline)

            // About
            SectionHeader("About")
            Text("BabyMomo v1.0.0", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface)
            Text("A private AI companion for Android", style = MaterialTheme.typography.bodySmall, color = DarkOnSurface)

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BabyPurple)
            ) { Text("Save Settings", color = DarkOnBg, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = BabyPurple, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ProviderCard(name: String, key: String, model: String, onKeyChange: (String) -> Unit, onModelChange: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, style = MaterialTheme.typography.titleSmall, color = BabyPurpleLight, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = key, onValueChange = onKeyChange,
                label = { Text("API Key") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = DarkOutline, textColor = DarkOnBg, focusedBorderColor = BabyPurple)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = model, onValueChange = onModelChange,
                label = { Text("Model") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = DarkOutline, textColor = DarkOnBg, focusedBorderColor = BabyPurple)
            )
        }
    }
}

@Composable
private fun SettingsNavItem(title: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceVariant
    ) {
        Text(title, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge, color = DarkOnBg)
    }
}
