package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Modos de tema visual soportados por MD GitHub Navigator.
 * - PAPER: Estilo GitHub Modern Clean con fondos nítidos y acentos en azul. (Predeterminado).
 * - SCHOLAR: Tono cálido marfil de lectura relajante para e-readers o noche.
 * - NEON_DEV: Modo oscuro con colores cian/magenta saturados para perfil desarrollador.
 */
enum class AppThemeMode {
    PAPER,
    SCHOLAR,
    NEON_DEV
}

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.PAPER }

@Composable
fun MdGhNavigatorTheme(
    themeMode: AppThemeMode = AppThemeMode.PAPER,
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
