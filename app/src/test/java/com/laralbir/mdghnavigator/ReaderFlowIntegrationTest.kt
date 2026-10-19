package com.laralbir.mdghnavigator

import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import com.laralbir.mdghnavigator.core.datastore.UserPreferences
import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import com.laralbir.mdghnavigator.core.renderer.HtmlTemplateBuilder
import com.laralbir.mdghnavigator.feature.annotations.MarkdownAnnotationExporter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReaderFlowIntegrationTest {

    @Test
    @DisplayName("End-to-End Reading Flow: Markdown parsing, rendering, bookmarking, annotations, export, and preference binding")
    fun testEndToEndReadingFlow() {
        val documentTitle = "Clean Architecture Guide.md"
        val repoId = "owner/clean-arch-sample"
        val docId = "doc_clean_arch_01"

        // 1. Raw Markdown document read by the user
        val rawMarkdown = """
            # Clean Architecture Guide
            
            Guía esencial para la separación de responsabilidades en Android.
            
            ## 📐 Principios de Dominio
            
            Las **reglas de negocio** deben ser independientes de frameworks UI y bases de datos.
            
            - Casos de uso atómicos (UseCases)
            - Repositorios desacoplados con contratos de interfaz
            
            ```kotlin
            class GetDocumentUseCase(private val repository: DocumentRepository) {
                operator fun invoke(id: String) = repository.getDocument(id)
            }
            ```
        """.trimIndent()

        // 2. Core Rendering: Parse and render raw Markdown into semantic HTML with Paper theme
        val templateBuilder = HtmlTemplateBuilder()
        val renderedHtml = templateBuilder.buildHtml(
            markdown = rawMarkdown,
            themeMode = AppThemeMode.PAPER,
            isDark = false,
            fontSizeSp = 18
        )

        // Verify HTML semantic rendering
        assertTrue(renderedHtml.contains("<h1 id=\"clean-architecture-guide\">Clean Architecture Guide</h1>"), "H1 title must be rendered with slug id")
        assertTrue(renderedHtml.contains("<h2 id=\"principios-de-dominio\">📐 Principios de Dominio</h2>"), "H2 section must be rendered with slug id")
        assertTrue(renderedHtml.contains("<strong>reglas de negocio</strong>"), "Bold text must be rendered as <strong>")
        assertTrue(renderedHtml.contains("<ul>"), "List must be rendered in <ul>")
        assertTrue(renderedHtml.contains("<li>Casos de uso atómicos (UseCases)</li>"), "List item must be rendered")
        assertTrue(renderedHtml.contains("<pre><code"), "Code block must be rendered inside <pre><code>")
        assertTrue(renderedHtml.contains("class GetDocumentUseCase"), "Code content must be preserved")
        assertTrue(renderedHtml.contains("--reader-bg: #FFFFFF"), "Paper theme background must be set")
        assertTrue(renderedHtml.contains("--reader-primary: #0969DA"), "GitHub blue accent must be set")
        assertTrue(renderedHtml.contains("--reader-text: #1F2328"), "Paper theme text color must be set")

        // 3. User starts reading and adds a dog-ear bookmark at 42% progress
        val bookmark = AnnotationEntity(
            id = "bm_001",
            documentId = docId,
            repoId = repoId,
            type = AnnotationType.BOOKMARK,
            title = "Domain Layer Section",
            note = "Important use case boundary rules",
            scrollProgress = 0.42f,
            scrollY = 1840
        )

        // 4. User highlights a critical paragraph
        val highlight = AnnotationEntity(
            id = "hl_001",
            documentId = docId,
            repoId = repoId,
            type = AnnotationType.HIGHLIGHT,
            selectedText = "Business rules should be completely independent of UI and database frameworks.",
            colorHex = "#FFD54F",
            scrollProgress = 0.45f,
            scrollY = 2100
        )

        // 5. User attaches a note to an architectural concept
        val note = AnnotationEntity(
            id = "nt_001",
            documentId = docId,
            repoId = repoId,
            type = AnnotationType.NOTE,
            selectedText = "Repository Pattern",
            note = "Implement offline-first repository with Room and ETag HTTP caching",
            scrollProgress = 0.55f,
            scrollY = 2650
        )

        val annotations = listOf(bookmark, highlight, note)

        // 6. Verify Markdown Export Output
        val exportedMarkdown = MarkdownAnnotationExporter.export(documentTitle, annotations)

        assertTrue(exportedMarkdown.contains("# Anotaciones y Marcadores: Clean Architecture Guide.md"))
        assertTrue(exportedMarkdown.contains("## 🔖 Marcadores"))
        assertTrue(exportedMarkdown.contains("- **Domain Layer Section** (Progreso: 42%)"))
        assertTrue(exportedMarkdown.contains("Important use case boundary rules"))
        assertTrue(exportedMarkdown.contains("## 🖍️ Resaltados"))
        assertTrue(exportedMarkdown.contains("Business rules should be completely independent"))
        assertTrue(exportedMarkdown.contains("## 📝 Notas"))
        assertTrue(exportedMarkdown.contains("Implement offline-first repository with Room and ETag HTTP caching"))

        // 7. Verify User Preferences & Theme Mode Resolution
        val prefs = UserPreferences(
            themeMode = "paper",
            fontScale = 1.15f,
            fontFamily = "inter",
            languageCode = "es"
        )

        val resolvedThemeMode = when (prefs.themeMode.lowercase()) {
            "scholar" -> AppThemeMode.SCHOLAR
            "neon", "neon_dev" -> AppThemeMode.NEON_DEV
            else -> AppThemeMode.PAPER
        }

        assertEquals(AppThemeMode.PAPER, resolvedThemeMode)
        assertEquals(1.15f, prefs.fontScale)
        assertEquals("inter", prefs.fontFamily)
        assertEquals("es", prefs.languageCode)
    }
}
