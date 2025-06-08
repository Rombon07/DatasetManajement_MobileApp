package com.example.data_manajemet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warna dasar
val PurplePrimary = Color(0xFF6A1B9A)
val PurpleSecondary = Color(0xFF9C27B0)
val White = Color(0xFFFFFFFF)
val LightGrayBackground = Color(0xFFF5F5F5)
val DarkText = Color(0xFF212121)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = White,
    secondary = PurpleSecondary,
    onSecondary = White,
    background = LightGrayBackground,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    error = Color(0xFFB00020),
    onError = White
)

@Composable
fun DataManajemetTheme(
    useDarkTheme: Boolean = false, // selalu terang
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
