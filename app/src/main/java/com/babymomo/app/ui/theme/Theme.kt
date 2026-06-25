package com.babymomo.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BabyPurple,
    onPrimary = DarkOnBg,
    primaryContainer = BabyPurpleDark,
    onPrimaryContainer = BabyPurpleLight,
    secondary = BabyPink,
    onSecondary = DarkOnBg,
    secondaryContainer = BabyPinkDark,
    onSecondaryContainer = Color(0xFFF48FB1),
    tertiary = Color(0xFF42A5F5),
    background = DarkBg,
    onBackground = DarkOnBg,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
    outline = DarkOutline,
    outlineVariant = Color(0xFF2A2A4A)
)

@Composable
fun BabyMomoTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBg.toArgb()
            window.navigationBarColor = DarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BabyMomoTypography,
        shapes = BabyMomoShapes,
        content = content
    )
}
