package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Modos de tema visual soportados por MD GitHub Navigator.
 * - SCHOLAR: Tono sepia y pergamino cálido. Máxima relajación ocular para lectura prolongada. (Predeterminado).
 * - PAPER: Estilo editorial minimalista blanco/negro inspirado en e-readers clásicos.
 * - NEON_DEV: Modo oscuro con colores cian/magenta saturados para perfil desarrollador.
 */
enum class AppThemeMode {
    SCHOLAR,
    PAPER,
    NEON_DEV
}

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.SCHOLAR }

@Composable
fun MdGhNavigatorTheme(
    themeMode: AppThemeMode = AppThemeMode.SCHOLAR,
    darkTheme: Boolean = isSystemInDarkTheme(),
    readerTypography: MdReaderTypography = MdReaderTypography(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.SCHOLAR -> if (darkTheme) ScholarDarkColors else ScholarLightColors
        AppThemeMode.PAPER -> if (darkTheme) PaperDarkColors else PaperLightColors
        AppThemeMode.NEON_DEV -> if (darkTheme) NeonDevDarkColors else NeonDevLightColors
    }

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalMdReaderTypography provides readerTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
