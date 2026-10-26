package com.komikta.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KomiktaColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    secondary = AccentSoft,
    onSecondary = OnAccent,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Color(0xFFEF5350),
    onError = OnAccent
)

@Composable
fun KomiktaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KomiktaColorScheme,
        typography = KomiktaTypography,
        content = content
    )
}
