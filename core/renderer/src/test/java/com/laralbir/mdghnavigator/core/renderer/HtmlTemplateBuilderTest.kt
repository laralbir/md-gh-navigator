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

        assertTrue(html.contains("--reader-bg: #F8FAFC"), "Should include Scholar light background color")
        assertTrue(html.contains("--reader-text: #0F172A"), "Should include Scholar light text color")
        assertTrue(html.contains("--reader-font-size: 18px"), "Should set 18px font size")
        assertTrue(html.contains("viewport"), "Must include mobile/tablet viewport meta tag")
        assertTrue(html.contains("Este es un documento de prueba"), "Raw markdown content must be embedded")
    }

    @Test
    @DisplayName("Scholar theme dark injects warm slate dark background")
    fun testScholarDarkHtml() {
        val html = builder.buildHtml(
            markdown = "Contenido nocturno",
            themeMode = AppThemeMode.SCHOLAR,
            isDark = true,
            fontSizeSp = 20
        )

        assertTrue(html.contains("--reader-bg: #0F172A"), "Should include Scholar dark background color")
        assertTrue(html.contains("--reader-text: #F8FAFC"), "Should include Scholar dark text color")
        assertTrue(html.contains("--reader-font-size: 20px"))
    }

    @Test
    @DisplayName("Paper theme light injects GitHub modern clean white and blue")
    fun testPaperLightHtml() {
        val html = builder.buildHtml(
            markdown = "Paper mode test",
            themeMode = AppThemeMode.PAPER,
            isDark = false,
            fontSizeSp = 18
        )

        assertTrue(html.contains("--reader-bg: #FFFFFF"), "Should include crisp white background")
        assertTrue(html.contains("--reader-text: #1F2328"), "Should include dark slate text color")
        assertTrue(html.contains("--reader-primary: #0969DA"), "Should include GitHub blue accent")
    }

    @Test
    @DisplayName("Paper theme dark injects GitHub modern dark background and blue accent")
    fun testPaperDarkHtml() {
        val html = builder.buildHtml(
            markdown = "Paper dark test",
            themeMode = AppThemeMode.PAPER,
            isDark = true,
            fontSizeSp = 18
        )

        assertTrue(html.contains("--reader-bg: #0D1117"), "Should include GitHub dark background")
        assertTrue(html.contains("--reader-text: #E6EDF3"), "Should include GitHub dark text")
        assertTrue(html.contains("--reader-primary: #58A6FF"), "Should include GitHub dark blue accent")
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

    @Test
    @DisplayName("Markdown headings, bold text, lists, and code blocks are rendered into semantic HTML")
    fun testMarkdownFormattingRenderedToHtml() {
        val markdown = """
            # Título Principal
            
            Este es un párrafo con **texto en negrita** y *cursiva*.
            
            - Elemento 1
            - Elemento 2
            
            ```kotlin
            val test = "hello"
            ```
        """.trimIndent()

        val html = builder.buildHtml(
            markdown = markdown,
            themeMode = AppThemeMode.SCHOLAR,
            isDark = false
        )

        assertTrue(html.contains("<h1>Título Principal</h1>"), "H1 heading must be rendered as <h1>")
        assertTrue(html.contains("<strong>texto en negrita</strong>"), "Bold text must be rendered as <strong>")
        assertTrue(html.contains("<em>cursiva</em>"), "Italic text must be rendered as <em>")
        assertTrue(html.contains("<ul>"), "Unordered list must be rendered as <ul>")
        assertTrue(html.contains("<li>Elemento 1</li>"), "List item must be rendered as <li>")
        assertTrue(html.contains("<code"), "Code snippet must be rendered in <code> tag")
    }

    @Test
    @DisplayName("HTML template links mermaid.min.js and marks dark mode attribute for diagram rendering")
    fun testMermaidScriptLinked() {
        val markdown = """
            ```mermaid
            graph TD
                A[Start] --> B[Finish]
            ```
        """.trimIndent()

        val html = builder.buildHtml(
            markdown = markdown,
            themeMode = AppThemeMode.PAPER,
            isDark = true
        )

        assertTrue(html.contains("mermaid.min.js"), "Must link mermaid.min.js")
        assertTrue(html.contains("data-is-dark=\"true\""), "Must declare dark mode for mermaid theme")
        assertTrue(html.contains("language-mermaid"), "Must contain mermaid code block")
    }
}

