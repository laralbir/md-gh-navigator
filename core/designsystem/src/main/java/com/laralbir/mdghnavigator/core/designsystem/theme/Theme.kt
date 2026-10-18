package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema Scholar (Sepia/Pergamino cálido — Optimizado para TCL NXTPAPER 11 Plus)
val ScholarLightColors = lightColorScheme(
    primary = Color(0xFF5D4037),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD7CCC8),
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFF795548),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF9F6F0),
    onBackground = Color(0xFF2C2523),
    surface = Color(0xFFF4EFEA),
    onSurface = Color(0xFF2C2523),
    surfaceVariant = Color(0xFFEBE3DB),
    onSurfaceVariant = Color(0xFF4E443F)
)

val ScholarDarkColors = darkColorScheme(
    primary = Color(0xFFD7CCC8),
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFF4E342E),
    onPrimaryContainer = Color(0xFFEFEBE9),
    secondary = Color(0xFFBCAAA4),
    onSecondary = Color(0xFF2C2523),
    background = Color(0xFF1E1A18),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF25211E),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF3B3430),
    onSurfaceVariant = Color(0xFFD0C4BA)
)

enum class AppThemeMode {
    SCHOLAR,
    PAPER,
    NEON_DEV
}

@Composable
fun MdGhNavigatorTheme(
    themeMode: AppThemeMode = AppThemeMode.SCHOLAR,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.SCHOLAR -> if (darkTheme) ScholarDarkColors else ScholarLightColors
        AppThemeMode.PAPER -> if (darkTheme) darkColorScheme() else lightColorScheme()
        AppThemeMode.NEON_DEV -> darkColorScheme(
            primary = Color(0xFF00E5FF),
            secondary = Color(0xFFFF007F),
            background = Color(0xFF0F111A),
            surface = Color(0xFF1A1C23)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

