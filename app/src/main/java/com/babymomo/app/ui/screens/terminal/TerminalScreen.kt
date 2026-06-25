package com.babymomo.app.ui.screens.terminal
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babymomo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(viewModel: TerminalViewModel = hiltViewModel()) {
    val output by viewModel.output.collectAsState()
    val input by viewModel.input.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(color = androidx.compose.ui.graphics.Color(0xFF1E1E1E))) {
        TopAppBar(
            title = { Text("Terminal", fontWeight = FontWeight.Bold, color = DarkOnBg) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color(0xFF2D2D2D)),
            actions = {
                IconButton(onClick = { viewModel.clear() }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = BabyPink)
                }
            }
        )
        LazyColumn(modifier = Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            items(output) { line ->
                Text(line, color = androidx.compose.ui.graphics.Color(0xFF00FF00), fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()))
            }
        }
        Surface(color = androidx.compose.ui.graphics.Color(0xFF2D2D2D)) {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("$ ", color = androidx.compose.ui.graphics.Color(0xFF00FF00), fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(value = input, onValueChange = { viewModel.updateInput(it) }, modifier = Modifier.weight(1f), placeholder = { Text("Enter command...", color = androidx.compose.ui.graphics.Color(0xFF666666)) }, singleLine = true, shape = RoundedCornerShape(4.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent), textColor = androidx.compose.ui.graphics.Color(0xFF00FF00))
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.execute() }, colors = ButtonDefaults.buttonColors(containerColor = BabyPurple), shape = RoundedCornerShape(4.dp)) { Text("Run", color = DarkOnBg) }
            }
        }
    }
}
