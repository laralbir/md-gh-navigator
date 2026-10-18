package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ThemeContrastTest {

    private fun linearize(channel: Float): Double {
        return if (channel <= 0.04045f) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).pow(2.4)
        }
    }

    private fun relativeLuminance(color: Color): Double {
        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(colorA: Color, colorB: Color): Double {
        val lumA = relativeLuminance(colorA)
        val lumB = relativeLuminance(colorB)
        val l1 = max(lumA, lumB)
        val l2 = min(lumA, lumB)
        return (l1 + 0.05) / (l2 + 0.05)
    }

    @Test
    @DisplayName("Scholar Light color scheme meets WCAG AA contrast for reading")
    fun testScholarLightContrast() {
        val bgRatio = contrastRatio(ScholarLightColors.background, ScholarLightColors.onBackground)
        val surfaceRatio = contrastRatio(ScholarLightColors.surface, ScholarLightColors.onSurface)

        assertTrue(bgRatio >= 7.0, "Scholar Light background contrast should exceed 7:1 for eReader clarity, got: $bgRatio")
        assertTrue(surfaceRatio >= 7.0, "Scholar Light surface contrast should exceed 7:1, got: $surfaceRatio")
    }

    @Test
    @DisplayName("Scholar Dark color scheme meets WCAG AA contrast for reading")
    fun testScholarDarkContrast() {
        val bgRatio = contrastRatio(ScholarDarkColors.background, ScholarDarkColors.onBackground)
        val surfaceRatio = contrastRatio(ScholarDarkColors.surface, ScholarDarkColors.onSurface)

        assertTrue(bgRatio >= 7.0, "Scholar Dark background contrast should exceed 7:1, got: $bgRatio")
        assertTrue(surfaceRatio >= 7.0, "Scholar Dark surface contrast should exceed 7:1, got: $surfaceRatio")
    }

    @Test
    @DisplayName("Paper Light color scheme meets WCAG AAA high contrast")
    fun testPaperLightContrast() {
        val bgRatio = contrastRatio(PaperLightColors.background, PaperLightColors.onBackground)
        assertTrue(bgRatio >= 7.0, "Paper Light background contrast should meet AAA (>= 7:1), got: $bgRatio")
    }

    @Test
    @DisplayName("Neon Dev color scheme meets WCAG AA contrast for code reading")
    fun testNeonDevContrast() {
        val bgRatio = contrastRatio(NeonDevDarkColors.background, NeonDevDarkColors.onBackground)
        assertTrue(bgRatio >= 4.5, "Neon Dev background contrast should meet AA (>= 4.5:1), got: $bgRatio")
    }
}
