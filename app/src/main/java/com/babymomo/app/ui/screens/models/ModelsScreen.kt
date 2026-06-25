package com.babymomo.app.ui.screens.models

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ModelsScreen(viewModel: ModelsViewModel = hiltViewModel()) {
    val models by viewModel.models.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("Models", fontWeight = FontWeight.Bold, color = DarkOnBg) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(models, key = { it.id }) { model ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(model.name, style = MaterialTheme.typography.titleMedium, color = DarkOnBg, fontWeight = FontWeight.Bold)
                            if (model.isActive) {
                                Surface(shape = RoundedCornerShape(4.dp), color = BabyPurple.copy(alpha = 0.3f)) {
                                    Text("ACTIVE", style = MaterialTheme.typography.labelSmall, color = BabyPurpleLight, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Size: ${formatSize(model.sizeBytes)}", style = MaterialTheme.typography.bodySmall, color = DarkOnSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEnd, horizontalAlignment = Alignment.CenterVertically) {
                            if (model.isDownloaded) {
                                TextButton(onClick = { viewModel.activateModel(model.id) }) {
                                    Text(if (model.isActive) "Active" else "Activate", color = if (model.isActive) DarkOnSurface else BabyPurple)
                                }
                            } else {
                                Button(onClick = { viewModel.downloadModel(model.id) }, colors = ButtonDefaults.buttonColors(containerColor = BabyPurple)) {
                                    Text("Download", color = DarkOnBg)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return if (gb >= 1) "%.1f GB".format(gb) else "%.0f MB".format(bytes / (1024.0 * 1024.0))
}
