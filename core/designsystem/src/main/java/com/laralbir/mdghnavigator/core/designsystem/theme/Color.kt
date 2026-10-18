package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// =========================================================================
// 1. TEMA SCHOLAR (Predeterminado — Optimizado para TCL NXTPAPER 11 Plus)
// Tonos cálidos, sepia y pergamino que minimizan la fatiga visual.
// =========================================================================

val ScholarLightColors = lightColorScheme(
    primary = Color(0xFF5D4037),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD7CCC8),
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFF795548),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEFEBE9),
    onSecondaryContainer = Color(0xFF4E342E),
    tertiary = Color(0xFF8D6E63),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFAF7F2),
    onBackground = Color(0xFF2C2420),
    surface = Color(0xFFF3EDE4),
    onSurface = Color(0xFF2C2420),
    surfaceVariant = Color(0xFFE7DFD5),
    onSurfaceVariant = Color(0xFF4F453E),
    outline = Color(0xFF85756C),
    outlineVariant = Color(0xFFD3C4B8)
)

val ScholarDarkColors = darkColorScheme(
    primary = Color(0xFFD7CCC8),
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFF4E342E),
    onPrimaryContainer = Color(0xFFEFEBE9),
    secondary = Color(0xFFBCAAA4),
    onSecondary = Color(0xFF2C2420),
    secondaryContainer = Color(0xFF3E3430),
    onSecondaryContainer = Color(0xFFD7CCC8),
    tertiary = Color(0xFFA1887F),
    onTertiary = Color(0xFF2C2420),
    background = Color(0xFF1A1715),
    onBackground = Color(0xFFEDE3D8),
    surface = Color(0xFF24201D),
    onSurface = Color(0xFFEDE3D8),
    surfaceVariant = Color(0xFF36302B),
    onSurfaceVariant = Color(0xFFD0C4BA),
    outline = Color(0xFF9C8F84),
    outlineVariant = Color(0xFF4F453E)
)

// =========================================================================
// 2. TEMA PAPER (Estilo libro impreso / editorial minimalista)
// Blanco/Negro y escala de grises con máximo contraste neutro.
// =========================================================================

val PaperLightColors = lightColorScheme(
    primary = Color(0xFF1F2328),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE1E4E8),
    onPrimaryContainer = Color(0xFF0D1117),
    secondary = Color(0xFF57606A),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1F2328),
    surface = Color(0xFFF6F8FA),
    onSurface = Color(0xFF1F2328),
    surfaceVariant = Color(0xFFEAEEF2),
    onSurfaceVariant = Color(0xFF57606A),
    outline = Color(0xFFD0D7DE),
    outlineVariant = Color(0xFFE1E4E8)
)

val PaperDarkColors = darkColorScheme(
    primary = Color(0xFFE6EDF3),
    onPrimary = Color(0xFF0D1117),
    primaryContainer = Color(0xFF30363D),
    onPrimaryContainer = Color(0xFFF0F6FC),
    secondary = Color(0xFF8B949E),
    onSecondary = Color(0xFF0D1117),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    outline = Color(0xFF30363D),
    outlineVariant = Color(0xFF21262D)
)

// =========================================================================
// 3. TEMA NEON DEV (Modo Hacker / IDE sintético)
// Paleta oscura saturada con cian, magenta y verde terminal.
// =========================================================================

val NeonDevDarkColors = darkColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004E58),
    onPrimaryContainer = Color(0xFF80F2FF),
    secondary = Color(0xFFFF007F),
    onSecondary = Color(0xFF40001B),
    secondaryContainer = Color(0xFF66002E),
    onSecondaryContainer = Color(0xFFFFB2D2),
    tertiary = Color(0xFF50FA7B),
    onTertiary = Color(0xFF003913),
    background = Color(0xFF0A0E14),
    onBackground = Color(0xFFECEFF4),
    surface = Color(0xFF121721),
    onSurface = Color(0xFFECEFF4),
    surfaceVariant = Color(0xFF1B2230),
    onSurfaceVariant = Color(0xFF8996A8),
    outline = Color(0xFF3B485E),
    outlineVariant = Color(0xFF232D3F)
)

val NeonDevLightColors = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2F1F8),
    onPrimaryContainer = Color(0xFF002025),
    secondary = Color(0xFFA80053),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF121721),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121721),
    surfaceVariant = Color(0xFFE2E7EE),
    onSurfaceVariant = Color(0xFF3B485E),
    outline = Color(0xFF6B7B94)
)
