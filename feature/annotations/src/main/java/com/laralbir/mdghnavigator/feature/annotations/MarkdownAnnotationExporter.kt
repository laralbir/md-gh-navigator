package com.laralbir.mdghnavigator.feature.annotations

import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType

/**
 * Utility for exporting annotations, bookmarks, and highlights to standard Markdown documents.
 */
object MarkdownAnnotationExporter {

    /**
     * Formats [annotations] belonging to [documentTitle] into a structured Markdown document.
     */
    fun export(
        documentTitle: String,
        annotations: List<AnnotationEntity>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("# Anotaciones y Marcadores: $documentTitle")
        sb.appendLine()

        if (annotations.isEmpty()) {
            sb.appendLine("*Sin anotaciones registradas en este documento.*")
            return sb.toString()
        }

        val bookmarks = annotations.filter { it.type == AnnotationType.BOOKMARK }
        val highlights = annotations.filter { it.type == AnnotationType.HIGHLIGHT }
        val notes = annotations.filter { it.type == AnnotationType.NOTE }

        if (bookmarks.isNotEmpty()) {
            sb.appendLine("## 🔖 Marcadores")
            sb.appendLine()
            for (bm in bookmarks) {
                val progressPercent = (bm.scrollProgress * 100).toInt()
                val title = bm.title ?: "Marcador"
                sb.appendLine("- **$title** (Progreso: $progressPercent%)")
                if (!bm.note.isNullOrBlank()) {
                    sb.appendLine("  - *Nota:* ${bm.note}")
                }
            }
            sb.appendLine()
        }

        if (highlights.isNotEmpty()) {
            sb.appendLine("## 🖍️ Resaltados")
            sb.appendLine()
            for (hl in highlights) {
                val progressPercent = (hl.scrollProgress * 100).toInt()
                sb.appendLine("> ${hl.selectedText}")
                sb.appendLine()
                sb.appendLine("*(Progreso: $progressPercent% - Color: ${hl.colorHex})*")
                sb.appendLine()
            }
        }

        if (notes.isNotEmpty()) {
            sb.appendLine("## 📝 Notas")
            sb.appendLine()
            for (note in notes) {
                val progressPercent = (note.scrollProgress * 100).toInt()
                if (note.selectedText.isNotBlank()) {
                    sb.appendLine("> ${note.selectedText}")
                    sb.appendLine()
                }
                sb.appendLine("- **Nota:** ${note.note.orEmpty()}")
                sb.appendLine("  *(Progreso: $progressPercent%)*")
                sb.appendLine()
            }
        }

        return sb.toString().trimEnd() + "\n"
    }
}
