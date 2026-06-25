package com.babymomo.app.ui.screens.heartbeat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babymomo.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartbeatScreen(viewModel: HeartbeatViewModel = hiltViewModel()) {
    val logs by viewModel.logs.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(title = { Text("Heartbeat Log", fontWeight = FontWeight.Bold, color = DarkOnBg) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            actions = { IconButton(onClick = { viewModel.triggerManual() }) { Icon(Icons.Default.Refresh, contentDescription = "Trigger", tint = BabyPurple) } } }
        )
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(logs, key = { it.id }) { log ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(SimpleDateFormat("HH:mm:ss, MMM d", Locale.getDefault()).format(Date(log.timestamp)), style = MaterialTheme.typography.labelMedium, color = DarkOnSurface)
                            Surface(shape = RoundedCornerShape(4.dp), color = if (log.notified) BabyPink.copy(alpha = 0.2f) else BabyPurple.copy(alpha = 0.2f)) {
                                Text(if (log.notified) "NOTIFIED" else "SILENT", style = MaterialTheme.typography.labelSmall, color = if (log.notified) BabyPink else BabyPurpleLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.summary, style = MaterialTheme.typography.bodyMedium, color = DarkOnBg)
                        if (log.message != null) { Text(log.message!!, style = MaterialTheme.typography.bodySmall, color = DarkOnSurface) }
                    }
                }
            }
        }
    }
}
