package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = CyanInfo,
    tertiary = GreenVerify,
    background = DeepSlateBg,
    surface = CardSlateBg,
    onPrimary = Color(0xFF1E1400),
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = ElevatedSlate,
    onSurfaceVariant = SoftGrayText,
    error = RedUrgent
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF9E7100),
    secondary = Color(0xFF007A8A),
    tertiary = Color(0xFF00833E),
    background = Color(0xFFF4F6FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF121824),
    onSurface = Color(0xFF121824),
    surfaceVariant = Color(0xFFECEFF5),
    onSurfaceVariant = Color(0xFF4A505F),
    error = Color(0xFFD32F2F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
