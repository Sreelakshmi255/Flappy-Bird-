package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.game.GameTheme

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyanBright,
    onPrimary = CyberBlack,
    primaryContainer = CyberVioletNeon,
    onPrimaryContainer = Color.White,
    secondary = CyberPinkNeon,
    onSecondary = Color.White,
    secondaryContainer = CyberVioletMid,
    background = CyberBlack,
    onBackground = CyberCyanBright,
    surface = CyberVioletDark,
    onSurface = Color.White,
    surfaceVariant = CyberVioletMid,
    onSurfaceVariant = CyberCyanBright,
    outline = CyberCyanNeon
)

private val DarkRetroColorScheme = darkColorScheme(
    primary = RetroYellow,
    onPrimary = Color.Black,
    primaryContainer = RetroOrange,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF38BDF8),
    onSecondary = Color.Black,
    background = RetroDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = RetroSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF272D45),
    onSurfaceVariant = Color(0xFFE2E8F0),
    outline = Color(0xFF475569)
)

private val ClassicColorScheme = lightColorScheme(
    primary = RetroGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9CE659),
    onPrimaryContainer = Color(0xFF1E3A0F),
    secondary = RetroOrange,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF0F172A)
)

@Composable
fun RetroFlapTheme(
    selectedTheme: GameTheme = GameTheme.CLASSIC_DAY,
    content: @Composable () -> Unit
) {
    val colorScheme = when (selectedTheme) {
        GameTheme.VIOLET_CYAN_EVENT -> CyberColorScheme
        GameTheme.DARK_NIGHT -> DarkRetroColorScheme
        GameTheme.CLASSIC_DAY -> ClassicColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
