package com.babymomo.app.ui.screens.interactive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.babymomo.app.core.interactive.InteractiveScreenParser
import com.babymomo.app.core.interactive.ScreenDescriptor
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
            InteractiveScreenRenderer(descriptor = descriptor, modifier = Modifier.padding(padding))
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Invalid screen descriptor", color = DarkOnSurface)
            }
        }
    }
}

@Composable
private fun InteractiveScreenRenderer(descriptor: ScreenDescriptor, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        descriptor.widgets.forEach { widget ->
            when (widget) {
                is com.babymomo.app.core.interactive.BabyWidget.Text -> {
                    Text(
                        text = widget.content,
                        style = when (widget.style) {
                            "h1" -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = DarkOnBg)
                            "h2" -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold, color = DarkOnBg)
                            "h3" -> MaterialTheme.typography.titleLarge.copy(color = DarkOnBg)
                            "body" -> MaterialTheme.typography.bodyLarge.copy(color = DarkOnSurface)
                            else -> MaterialTheme.typography.bodyMedium.copy(color = DarkOnSurface)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                is com.babymomo.app.core.interactive.BabyWidget.Button -> {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = BabyPurple),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(widget.label, color = DarkOnBg)
                    }
                }
                is com.babymomo.app.core.interactive.BabyWidget.Input -> {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        label = { Text(widget.placeholder) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BabyPurple,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            cursorColor = BabyPurple
                        )
                    )
                }
                is com.babymomo.app.core.interactive.BabyWidget.Progress -> {
                    LinearProgressIndicator(
                        progress = { widget.value.toFloat() / 100f },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        color = BabyPurple,
                        trackColor = DarkSurfaceVariant
                    )
                }
                is com.babymomo.app.core.interactive.BabyWidget.Spacer -> {
                    Spacer(modifier = Modifier.height(widget.heightDp.dp))
                }
                is com.babymomo.app.core.interactive.BabyWidget.Row -> {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        widget.children.forEach { childWidget ->
                            InteractiveScreenRenderer(
                                descriptor = ScreenDescriptor(title = "", widgets = listOf(childWidget)),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}