package com.laralbir.mdghnavigator.core.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TypographyTest {

    @Test
    @DisplayName("Reader typography enforces serif font for prose and monospace for code")
    fun testFontFamilies() {
        val typography = MdReaderTypography()

        assertEquals(FontFamily.Serif, typography.bodyLarge.fontFamily)
        assertEquals(FontFamily.Serif, typography.bodyMedium.fontFamily)
        assertEquals(FontFamily.Serif, typography.heading1.fontFamily)
        assertEquals(FontFamily.Serif, typography.heading2.fontFamily)
        assertEquals(FontFamily.Serif, typography.heading3.fontFamily)
        assertEquals(FontFamily.Monospace, typography.codeBlock.fontFamily)
    }

    @Test
    @DisplayName("Reader typography enforces readable line spacing and font hierarchy")
    fun testTypographyHierarchyAndSpacing() {
        val typography = MdReaderTypography()

        // Hierarchy
        assertTrue(typography.heading1.fontSize.value > typography.heading2.fontSize.value)
        assertTrue(typography.heading2.fontSize.value > typography.heading3.fontSize.value)
        assertTrue(typography.heading3.fontSize.value > typography.bodyLarge.fontSize.value)
        assertTrue(typography.bodyLarge.fontSize.value >= 18f, "Body font size must be at least 18sp for eReader clarity")

        // Comfortable line height for long reading sessions
        assertTrue(typography.bodyLarge.lineHeight.value >= typography.bodyLarge.fontSize.value * 1.3f)
    }
}
