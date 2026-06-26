package com.babymomo.app.ui.screens.interactive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.babymomo.app.core.interactive.InteractiveScreenParser
import com.babymomo.app.core.interactive.BabyWidget
import com.babymomo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveScreen(descriptorJson: String, navController: NavController? = null) {
    val parser = remember { InteractiveScreenParser() }
    val descriptor = remember(descriptorJson) { parser.parse(descriptorJson) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(descriptor?.title ?: "Interactive", color = DarkOnBg) },
                navigationIcon = {
                    if (navController != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkOnBg)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        if (descriptor != null) {
            WidgetList(widgets = descriptor.widgets, modifier = Modifier.padding(padding))
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Invalid screen descriptor", color = DarkOnSurface)
            }
        }
    }
}

@Composable
private fun WidgetList(widgets: List<BabyWidget>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        widgets.forEach { widget ->
            WidgetItem(widget = widget)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WidgetItem(widget: BabyWidget) {
    when (widget) {
        is BabyWidget.BabyText -> {
            Text(
                text = widget.text,
                style = when (widget.style) {
                    "h1" -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = DarkOnBg)
                    "h2" -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold, color = DarkOnBg)
                    "h3" -> MaterialTheme.typography.titleLarge.copy(color = DarkOnBg)
                    "body" -> MaterialTheme.typography.bodyLarge.copy(color = DarkOnSurface)
                    else -> MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurface)
                }
            )
        }
        is BabyWidget.BabyButton -> {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = BabyPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(widget.label, color = DarkOnBg)
            }
        }
        is BabyWidget.BabyInput -> {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text(widget.hint) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BabyPurple,
                    unfocusedBorderColor = DarkSurfaceVariant,
                    cursorColor = BabyPurple
                )
            )
        }
        is BabyWidget.BabyProgress -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (widget.label.isNotBlank()) {
                    Text(widget.label, style = MaterialTheme.typography.bodySmall, color = DarkOnSurface)
                }
                LinearProgressIndicator(
                    progress = { if (widget.max > 0) widget.value.toFloat() / widget.max.toFloat() else 0f },
                    modifier = Modifier.fillMaxWidth(),
                    color = BabyPurple,
                    trackColor = DarkSurfaceVariant
                )
            }
        }
        is BabyWidget.BabyDivider -> {
            HorizontalDivider(color = DarkSurfaceVariant, modifier = Modifier.fillMaxWidth())
        }
        is BabyWidget.BabyList -> {
            widget.items.forEach { item ->
                TextButton(onClick = { }) {
                    Text(item.text, color = BabyPurple)
                }
            }
        }
        is BabyWidget.BabyCard -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (widget.title.isNotBlank()) {
                        Text(widget.title, style = MaterialTheme.typography.titleMedium, color = DarkOnBg)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (widget.body.isNotBlank()) {
                        Text(widget.body, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface)
                    }
                    if (widget.children.isNotEmpty()) {
                        WidgetList(widgets = widget.children, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
        is BabyWidget.BabyGrid -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                widget.children.chunked(widget.columns.coerceAtLeast(1)).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { child ->
                            Box(modifier = Modifier.weight(1f)) {
                                WidgetItem(widget = child)
                            }
                        }
                    }
                }
            }
        }
    }
}