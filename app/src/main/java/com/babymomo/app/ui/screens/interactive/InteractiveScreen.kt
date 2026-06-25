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
import com.babymomo.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveScreen(descriptorJson: String, navController: NavController? = null, parser: InteractiveScreenParser) {
    val descriptor = remember(descriptorJson) { parser.parse(descriptorJson) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text(descriptor?.title ?: "Interactive", fontWeight = FontWeight.Bold, color = DarkOnBg) },
            navigationIcon = { IconButton(onClick = { navController?.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkOnBg) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )
        if (descriptor != null) {
            InteractiveScreenRenderer(descriptor) { actionId ->
                navController?.popBackStack()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Could not parse screen descriptor", color = DarkOnSurface)
            }
        }
    }
}