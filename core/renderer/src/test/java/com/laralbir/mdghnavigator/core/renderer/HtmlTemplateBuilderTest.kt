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

        assertTrue(html.contains("<h1 id=\"título-principal\">Título Principal</h1>"), "H1 heading must be rendered with slug id attribute")
        assertTrue(html.contains("<strong>texto en negrita</strong>"), "Bold text must be rendered as <strong>")
        assertTrue(html.contains("<em>cursiva</em>"), "Italic text must be rendered as <em>")
        assertTrue(html.contains("<ul>"), "Unordered list must be rendered as <ul>")
        assertTrue(html.contains("<li>Elemento 1</li>"), "List item must be rendered as <li>")
        assertTrue(html.contains("<code"), "Code snippet must be rendered in <code> tag")
    }

    @Test
    @DisplayName("Markdown headings receive GitHub-compatible unique slug IDs for anchor navigation")
    fun testHeadingSlugGenerationAndDeduplication() {
        val markdown = """
            # Introducción General
            ## 1. ¿Cómo empezar? (Guía Rápida)
            ### Subsección
            ### Subsección
            ### Subsección
            ## Enlaces y anclas: [Detalles](http://example.com)
        """.trimIndent()

        val html = builder.buildHtml(
            markdown = markdown,
            themeMode = AppThemeMode.PAPER,
            isDark = false
        )

        assertTrue(html.contains("<h1 id=\"introducción-general\">"), "Should generate slug with unicode letters")
        assertTrue(html.contains("<h2 id=\"1-cómo-empezar-guía-rápida\">"), "Should strip punctuation and symbols")
        assertTrue(html.contains("<h3 id=\"subsección\">"), "First occurrence should have base slug")
        assertTrue(html.contains("<h3 id=\"subsección-1\">"), "Second occurrence should have -1 suffix")
        assertTrue(html.contains("<h3 id=\"subsección-2\">"), "Third occurrence should have -2 suffix")
        assertTrue(html.contains("<h2 id=\"enlaces-y-anclas-detalles\">"), "Should extract text from nested nodes inside heading")
    }

    @Test
    @DisplayName("Markdown tables are rendered with table-wrapper and overflow-x CSS for wide horizontal scrolling")
    fun testMarkdownTableScrollable() {
        val markdown = """
            | Cabecera 1 | Cabecera 2 | Cabecera 3 | Cabecera 4 |
            | :--- | :--- | :--- | :--- |
            | Dato 1 | Dato 2 | Dato 3 | Dato 4 |
        """.trimIndent()

        val html = builder.buildHtml(
            markdown = markdown,
            themeMode = AppThemeMode.PAPER,
            isDark = false
        )

        assertTrue(html.contains("<table>"), "Must contain table tag")
        assertTrue(html.contains("<th"), "Must render table header tag")
        assertTrue(html.contains("Cabecera 1"), "Must render header text")
        assertTrue(html.contains("<td"), "Must render table cell tag")
        assertTrue(html.contains("Dato 1"), "Must render cell text")
        assertTrue(html.contains(".table-wrapper"), "Must define table-wrapper style")
        assertTrue(html.contains("overflow-x: auto"), "Must enable horizontal scrolling")
        assertTrue(html.contains("white-space: nowrap"), "Must prevent cell text wrapping on wide tables")
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

