package com.laralbir.mdghnavigator.feature.annotations

import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MarkdownAnnotationExporterTest {

    @Test
    @DisplayName("Exporting empty annotations list returns valid markdown header")
    fun testExportEmptyList() {
        val markdown = MarkdownAnnotationExporter.export(
            documentTitle = "Guía de Arquitectura",
            annotations = emptyList()
        )
        assertTrue(markdown.contains("# Anotaciones y Marcadores: Guía de Arquitectura"))
        assertTrue(markdown.contains("Sin anotaciones registradas"))
    }

    @Test
    @DisplayName("Exporting bookmarks, highlights, and notes produces formatted sections")
    fun testExportPopulatedList() {
        val annotations = listOf(
            AnnotationEntity(
                id = "b1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.BOOKMARK,
                title = "Capítulo 3: Concurrencia",
                note = "Repasar antes del sprint",
                scrollProgress = 0.35f,
                scrollY = 400
            ),
            AnnotationEntity(
                id = "h1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.HIGHLIGHT,
                selectedText = "Las corrutinas son hilos ligeros gestionados en user-space.",
                colorHex = "#FFEB3B",
                scrollProgress = 0.40f,
                scrollY = 450
            ),
            AnnotationEntity(
                id = "n1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.NOTE,
                selectedText = "Dispatchers.IO está respaldado por un pool elástico.",
                note = "Verificar límite de 64 threads por defecto.",
                colorHex = "#81C784",
                scrollProgress = 0.55f,
                scrollY = 620
            )
        )

        val markdown = MarkdownAnnotationExporter.export(
            documentTitle = "Kotlin Coroutines",
            annotations = annotations
        )

        // Bookmarks section
        assertTrue(markdown.contains("## 🔖 Marcadores"))
        assertTrue(markdown.contains("Capítulo 3: Concurrencia"))
        assertTrue(markdown.contains("Repasar antes del sprint"))
        assertTrue(markdown.contains("Progreso: 35%"))

        // Highlights section
        assertTrue(markdown.contains("## 🖍️ Resaltados"))
        assertTrue(markdown.contains("> Las corrutinas son hilos ligeros gestionados en user-space."))

        // Notes section
        assertTrue(markdown.contains("## 📝 Notas"))
        assertTrue(markdown.contains("> Dispatchers.IO está respaldado por un pool elástico."))
        assertTrue(markdown.contains("Verificar límite de 64 threads por defecto."))
    }
}
