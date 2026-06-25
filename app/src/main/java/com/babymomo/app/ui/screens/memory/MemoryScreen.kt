package com.babymomo.app.ui.screens.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
fun MemoryScreen(viewModel: MemoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val types = listOf("ALL", "WORKING", "EPISODIC", "SEMANTIC", "PROCEDURAL")

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Memory", fontWeight = FontWeight.Bold, color = DarkOnBg) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        // Stats card
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Active", uiState.activeCount.toString(), BabyPurple)
                StatItem("Total", uiState.totalCount.toString(), BabyPink)
                StatItem("Entities", uiState.entityCount.toString(), MemoryWorking)
                StatItem("Promoted", uiState.promotedCount.toString(), MemoryEpisodic)
            }
        }

        // Search bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.search(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Search memories...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = DarkOutline, textColor = DarkOnBg
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Type filter tabs
        ScrollableTabRow(
            selectedTabIndex = types.indexOf(uiState.selectedType).coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = DarkOnBg,
            edgePadding = 16.dp
        ) {
            types.forEach { type ->
                Tab(
                    selected = uiState.selectedType == type,
                    onClick = { viewModel.selectType(type) },
                    text = { Text(type, fontWeight = if (uiState.selectedType == type) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Memory list
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.memories, key = { it.id }) { memory ->
                MemoryCard(memory = memory, onDelete = { viewModel.deleteMemory(memory) })
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = DarkOnSurface)
    }
}

@Composable
private fun MemoryCard(memory: com.babymomo.app.data.db.entities.MemoryEntity, onDelete: () -> Unit) {
    val typeColor = when (memory.type) {
        "WORKING" -> MemoryWorking
        "EPISODIC" -> MemoryEpisodic
        "SEMANTIC" -> MemorySemantic
        "PROCEDURAL" -> MemoryProcedural
        else -> BabyPurple
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(4.dp), color = typeColor.copy(alpha = 0.2f)) {
                    Text(memory.type, style = MaterialTheme.typography.labelSmall, color = typeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Row {
                    Text("Hits: ${memory.hitCount}", style = MaterialTheme.typography.labelSmall, color = DarkOnSurface)
                    if (memory.isInSystemPrompt) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = BabyPurple.copy(alpha = 0.3f)) {
                            Text("PROMOTED", style = MaterialTheme.typography.labelSmall, color = BabyPurpleLight, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(memory.content, style = MaterialTheme.typography.bodyMedium, color = DarkOnBg)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Conf: ${(memory.confidence * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = DarkOnSurface)
                TextButton(onClick = onDelete) { Text("Delete", color = BabyPink, style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}
