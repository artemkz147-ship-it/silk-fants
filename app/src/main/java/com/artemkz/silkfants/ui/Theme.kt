package com.artemkz.silkfants.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val scheme = darkColorScheme(
    primary = Color(0xFFE85A8C),
    onPrimary = Color.White,
    secondary = Color(0xFFAD1457),
    background = Color(0xFF1A0A14),
    surface = Color(0xFF2D1528),
    onBackground = Color(0xFFFFE4EC),
    onSurface = Color(0xFFFFE4EC),
)

@Composable
fun SilkFantsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
