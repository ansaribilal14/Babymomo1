package com.babymomo.app.ui.screens.interactive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.babymomo.app.core.interactive.BabyWidget
import com.babymomo.app.core.interactive.ScreenDescriptor
import com.babymomo.app.ui.theme.*

@Composable
fun InteractiveScreenRenderer(descriptor: ScreenDescriptor, onAction: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (descriptor.title.isNotBlank()) {
            Text(descriptor.title, style = MaterialTheme.typography.headlineMedium, color = DarkOnBg, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(descriptor.widgets) { widget ->
                RenderWidget(widget, onAction)
            }
        }
    }
}

@Composable
private fun RenderWidget(widget: BabyWidget, onAction: (String) -> Unit) {
    when (widget) {
        is BabyWidget.BabyText -> {
            val style = when (widget.style) {
                "title" -> MaterialTheme.typography.headlineSmall
                "caption" -> MaterialTheme.typography.bodySmall
                else -> MaterialTheme.typography.bodyLarge
            }
            Text(widget.text, style = style, color = DarkOnBg, fontWeight = if (widget.style == "title") FontWeight.Bold else FontWeight.Normal)
        }
        is BabyWidget.BabyButton -> {
            Button(onClick = { onAction(widget.actionId) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = BabyPurple), shape = RoundedCornerShape(12.dp)) {
                Text(widget.label, color = DarkOnBg, fontWeight = FontWeight.Bold)
            }
        }
        is BabyWidget.BabyList -> {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    widget.items.forEach { item ->
                        TextButton(onClick = { item.actionId?.let { onAction(it) } }, modifier = Modifier.fillMaxWidth()) {
                            Text(item.text, color = DarkOnBg, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                        }
                    }
                }
            }
        }
        is BabyWidget.BabyCard -> {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(widget.title, style = MaterialTheme.typography.titleMedium, color = DarkOnBg, fontWeight = FontWeight.Bold)
                    if (widget.body.isNotBlank()) { Spacer(modifier = Modifier.height(4.dp)); Text(widget.body, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface) }
                }
            }
        }
        is BabyWidget.BabyGrid -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                widget.children.chunked(widget.columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { child ->
                            Box(modifier = Modifier.weight(1f)) { RenderWidget(child, onAction) }
                        }
                    }
                }
            }
        }
        is BabyWidget.BabyProgress -> {
            Column {
                Text(widget.label, style = MaterialTheme.typography.bodyMedium, color = DarkOnBg)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(progress = { widget.value.toFloat() / widget.max.coerceAtLeast(1) }, modifier = Modifier.fillMaxWidth(), color = BabyPurple, trackColor = DarkOutline)
            }
        }
        is BabyWidget.BabyDivider -> { HorizontalDivider(color = DarkOutline, modifier = Modifier.padding(vertical = 8.dp)) }
        is BabyWidget.BabyInput -> {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text(widget.hint) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = DarkOutline))
        }
    }
}
