package com.laralbir.mdghnavigator.core.renderer

import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HtmlTemplateBuilderTest {

    private val builder = HtmlTemplateBuilder()

    @Test
    @DisplayName("Scholar theme light injects sepia background and high-contrast text CSS")
    fun testScholarLightHtml() {
        val markdown = "# Bienvenido a MD GitHub Navigator\nEste es un documento de prueba."
        val html = builder.buildHtml(
            markdown = markdown,
            themeMode = AppThemeMode.SCHOLAR,
            isDark = false,
            fontSizeSp = 18
        )

        assertTrue(html.contains("--reader-bg: #FAF7F2"), "Should include Scholar light background color")
        assertTrue(html.contains("--reader-text: #2C2420"), "Should include Scholar light text color")
        assertTrue(html.contains("--reader-font-size: 18px"), "Should set 18px font size")
        assertTrue(html.contains("viewport"), "Must include mobile/tablet viewport meta tag")
        assertTrue(html.contains("Este es un documento de prueba"), "Raw markdown content must be embedded")
    }

    @Test
    @DisplayName("Scholar theme dark injects warm dark background")
    fun testScholarDarkHtml() {
        val html = builder.buildHtml(
            markdown = "Contenido nocturno",
            themeMode = AppThemeMode.SCHOLAR,
            isDark = true,
            fontSizeSp = 20
        )

        assertTrue(html.contains("--reader-bg: #1A1715"), "Should include Scholar dark background color")
        assertTrue(html.contains("--reader-text: #EDE3D8"), "Should include Scholar dark text color")
        assertTrue(html.contains("--reader-font-size: 20px"))
    }

    @Test
    @DisplayName("Neon Dev theme injects cyberpunk dark palette")
    fun testNeonDevHtml() {
        val html = builder.buildHtml(
            markdown = "```kotlin\nval x = 1\n```",
            themeMode = AppThemeMode.NEON_DEV,
            isDark = true,
            fontSizeSp = 16
        )

        assertTrue(html.contains("--reader-bg: #0A0E14"), "Should include Neon Dev dark background")
        assertTrue(html.contains("--reader-primary: #00E5FF"), "Should include Neon Dev cyan primary")
    }

    @Test
    @DisplayName("HTML template embeds local script tags for offline rendering")
    fun testLocalScriptTags() {
        val html = builder.buildHtml(
            markdown = "Test",
            themeMode = AppThemeMode.SCHOLAR,
            isDark = false,
            fontSizeSp = 18
        )

        assertTrue(html.contains("reader.js"), "Must link to local reader.js")
        assertTrue(html.contains("AndroidBridge"), "Must reference AndroidBridge communication interface")
    }
}

