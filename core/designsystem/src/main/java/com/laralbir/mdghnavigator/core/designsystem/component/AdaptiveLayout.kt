package com.laralbir.mdghnavigator.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class LayoutType {
    COMPACT,
    MEDIUM,
    EXPANDED
}

data class ReaderWindowLayout(
    val type: LayoutType,
    val isTwoPaneSplitEnabled: Boolean,
    val isTabletOrDesktop: Boolean,
    val contentHorizontalPadding: Dp,
    val splitPaneRatio: Float = 0.28f
)

/**
 * Determina la configuración de diseño responsivo y adaptativo.
 * Diseñado específicamente para optimizar la experiencia en tablets como TCL NXTPAPER 11 Plus
 * (con pantalla 3:2, 11.5 pulgadas) y dispositivos móviles estándar.
 */
fun computeReaderWindowLayout(screenWidthDp: Int): ReaderWindowLayout {
    return when {
        screenWidthDp >= 840 -> ReaderWindowLayout(
            type = LayoutType.EXPANDED,
            isTwoPaneSplitEnabled = true,
            isTabletOrDesktop = true,
            contentHorizontalPadding = 32.dp,
            splitPaneRatio = 0.28f
        )
        screenWidthDp >= 600 -> ReaderWindowLayout(
            type = LayoutType.MEDIUM,
            isTwoPaneSplitEnabled = false,
            isTabletOrDesktop = true,
            contentHorizontalPadding = 24.dp
        )
        else -> ReaderWindowLayout(
            type = LayoutType.COMPACT,
            isTwoPaneSplitEnabled = false,
            isTabletOrDesktop = false,
            contentHorizontalPadding = 16.dp
        )
    }
}

@Composable
fun rememberReaderWindowLayout(): ReaderWindowLayout {
    val configuration = LocalConfiguration.current
    return computeReaderWindowLayout(configuration.screenWidthDp)
}
