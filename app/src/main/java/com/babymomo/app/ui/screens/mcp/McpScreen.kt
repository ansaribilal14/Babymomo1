package com.babymomo.app.ui.screens.mcp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babymomo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McpScreen(viewModel: McpViewModel = hiltViewModel()) {
    val curated = remember { viewModel.getCuratedServers() }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(title = { Text("MCP Servers", fontWeight = FontWeight.Bold, color = DarkOnBg) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(curated, key = { it.id }) { server ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(server.name, style = MaterialTheme.typography.titleMedium, color = DarkOnBg, fontWeight = FontWeight.Bold)
                            if (server.isCurated) {
                                Surface(shape = RoundedCornerShape(4.dp), color = BabyPurple.copy(alpha = 0.2f)) {
                                    Text("CURATED", style = MaterialTheme.typography.labelSmall, color = BabyPurpleLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text(server.url, style = MaterialTheme.typography.bodySmall, color = DarkOnSurface)
                    }
                }
            }
        }
    }
}
