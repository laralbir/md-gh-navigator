package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// =========================================================================
// 1. TEMA SCHOLAR (Estilo lectura cálida / Papel marfil relajante)
// Paleta suave inspirada en e-readers modernos, sin tonos marrones oscuros.
// =========================================================================

val ScholarLightColors = lightColorScheme(
    primary = Color(0xFF4A5568),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = Color(0xFF1A202C),
    secondary = Color(0xFF718096),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF7FAFC),
    onSecondaryContainer = Color(0xFF2D3748),
    tertiary = Color(0xFF319795),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFBF9F5),
    onBackground = Color(0xFF2D3748),
    surface = Color(0xFFF3EFE6),
    onSurface = Color(0xFF2D3748),
    surfaceVariant = Color(0xFFE8E2D5),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFA0AEC0),
    outlineVariant = Color(0xFFCBD5E0)
)

val ScholarDarkColors = darkColorScheme(
    primary = Color(0xFFCBD5E0),
    onPrimary = Color(0xFF1A202C),
    primaryContainer = Color(0xFF2D3748),
    onPrimaryContainer = Color(0xFFF7FAFC),
    secondary = Color(0xFFA0AEC0),
    onSecondary = Color(0xFF1A202C),
    secondaryContainer = Color(0xFF232A36),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF4FD1C5),
    onTertiary = Color(0xFF1A202C),
    background = Color(0xFF1A202C),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF232A36),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF2D3748),
    onSurfaceVariant = Color(0xFFA0AEC0),
    outline = Color(0xFF4A5568),
    outlineVariant = Color(0xFF2D3748)
)

// =========================================================================
// 2. TEMA PAPER (Predeterminado — GitHub Modern Clean)
// Fondo blanco nítido / gris suave con acentos en azul GitHub moderno.
// Máximo contraste, frescura y legibilidad técnica impecable.
// =========================================================================

val PaperLightColors = lightColorScheme(
    primary = Color(0xFF0969DA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = Color(0xFF0969DA),
    secondary = Color(0xFF57606A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF6F8FA),
    onSecondaryContainer = Color(0xFF1F2328),
    tertiary = Color(0xFF1F883D),
    onTertiary = Color(0xFFFFFFFF),
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
    primary = Color(0xFF58A6FF),
    onPrimary = Color(0xFF0D1117),
    primaryContainer = Color(0xFF1F6FEB),
    onPrimaryContainer = Color(0xFFF0F6FC),
    secondary = Color(0xFF8B949E),
    onSecondary = Color(0xFF0D1117),
    secondaryContainer = Color(0xFF21262D),
    onSecondaryContainer = Color(0xFFE6EDF3),
    tertiary = Color(0xFF3FB950),
    onTertiary = Color(0xFF0D1117),
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
