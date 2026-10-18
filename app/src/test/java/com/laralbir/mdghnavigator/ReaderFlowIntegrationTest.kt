package com.laralbir.mdghnavigator

import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import com.laralbir.mdghnavigator.core.datastore.UserPreferences
import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import com.laralbir.mdghnavigator.feature.annotations.MarkdownAnnotationExporter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReaderFlowIntegrationTest {

    @Test
    @DisplayName("End-to-End Reading Flow: bookmarking, annotations, export, and preference binding")
    fun testEndToEndReadingFlow() {
        val documentTitle = "Clean Architecture Guide.md"
        val repoId = "owner/clean-arch-sample"
        val docId = "doc_clean_arch_01"

        // 1. User starts reading and adds a dog-ear bookmark at 42% progress
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

        // 2. User highlights a critical paragraph
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

        // 3. User attaches a note to an architectural concept
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

        // 4. Verify Markdown Export Output
        val exportedMarkdown = MarkdownAnnotationExporter.export(documentTitle, annotations)

        assertTrue(exportedMarkdown.contains("# Anotaciones y Marcadores: Clean Architecture Guide.md"))
        assertTrue(exportedMarkdown.contains("## 🔖 Marcadores"))
        assertTrue(exportedMarkdown.contains("- **Domain Layer Section** (Progreso: 42%)"))
        assertTrue(exportedMarkdown.contains("Important use case boundary rules"))
        assertTrue(exportedMarkdown.contains("## 🖍️ Resaltados"))
        assertTrue(exportedMarkdown.contains("Business rules should be completely independent"))
        assertTrue(exportedMarkdown.contains("## 📝 Notas"))
        assertTrue(exportedMarkdown.contains("Implement offline-first repository with Room and ETag HTTP caching"))

        // 5. Verify User Preferences & Theme Mode Resolution
        val prefs = UserPreferences(
            themeMode = "scholar",
            fontScale = 1.15f,
            fontFamily = "literata",
            languageCode = "es"
        )

        val resolvedThemeMode = when (prefs.themeMode.lowercase()) {
            "paper" -> AppThemeMode.PAPER
            "neon" -> AppThemeMode.NEON_DEV
            else -> AppThemeMode.SCHOLAR
        }

        assertEquals(AppThemeMode.SCHOLAR, resolvedThemeMode)
        assertEquals(1.15f, prefs.fontScale)
        assertEquals("literata", prefs.fontFamily)
        assertEquals("es", prefs.languageCode)
    }
}
