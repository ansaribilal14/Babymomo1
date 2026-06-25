package com.babymomo.app.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babymomo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column {
            TopAppBar(
                title = { Text("Projects", fontWeight = FontWeight.Bold, color = DarkOnBg) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.projects, key = { it.id }) { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(project.name, style = MaterialTheme.typography.titleMedium, color = DarkOnBg, fontWeight = FontWeight.Bold)
                                Surface(shape = RoundedCornerShape(4.dp), color = BabyPurple.copy(alpha = 0.2f)) {
                                    Text(project.status, style = MaterialTheme.typography.labelSmall, color = BabyPurpleLight, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                            if (!project.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(project.description, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface)
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { viewModel.showCreate() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = BabyPurple
        ) { Icon(Icons.Default.Add, contentDescription = "Create Project", tint = DarkOnBg) }

        if (uiState.showCreateDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideCreate() },
                title = { Text("Create Project") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = uiState.newProjectName, onValueChange = { viewModel.updateName(it) }, label = { Text("Project Name") }, singleLine = true)
                        OutlinedTextField(value = uiState.newProjectDesc, onValueChange = { viewModel.updateDesc(it) }, label = { Text("Description") }, maxLines = 3)
                    }
                },
                confirmButton = { TextButton(onClick = { viewModel.createProject() }) { Text("Create", color = BabyPurple) } },
                dismissButton = { TextButton(onClick = { viewModel.hideCreate() }) { Text("Cancel") } }
            )
        }
    }
}
