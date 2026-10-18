package com.laralbir.mdghnavigator.feature.annotations

import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType

enum class AnnotationFilterTab {
    ALL,
    BOOKMARKS,
    HIGHLIGHTS,
    NOTES
}

data class AnnotationsUiState(
    val documentId: String = "",
    val repoId: String = "",
    val annotations: List<AnnotationEntity> = emptyList(),
    val selectedFilter: AnnotationFilterTab = AnnotationFilterTab.ALL,
    val isLoading: Boolean = false,
    val exportedMarkdown: String? = null,
    val isAddBookmarkDialogOpen: Boolean = false
) {
    val filteredAnnotations: List<AnnotationEntity>
        get() = when (selectedFilter) {
            AnnotationFilterTab.ALL -> annotations
            AnnotationFilterTab.BOOKMARKS -> annotations.filter { it.type == AnnotationType.BOOKMARK }
            AnnotationFilterTab.HIGHLIGHTS -> annotations.filter { it.type == AnnotationType.HIGHLIGHT }
            AnnotationFilterTab.NOTES -> annotations.filter { it.type == AnnotationType.NOTE }
        }

    val bookmarksCount: Int
        get() = annotations.count { it.type == AnnotationType.BOOKMARK }

    val highlightsCount: Int
        get() = annotations.count { it.type == AnnotationType.HIGHLIGHT }

    val notesCount: Int
        get() = annotations.count { it.type == AnnotationType.NOTE }
}

sealed interface AnnotationsIntent {
    data class Load(val documentId: String, val repoId: String) : AnnotationsIntent
    data class SetFilter(val filter: AnnotationFilterTab) : AnnotationsIntent
    data class AddBookmark(
        val title: String,
        val scrollProgress: Float,
        val scrollY: Int,
        val note: String? = null
    ) : AnnotationsIntent
    data class AddHighlight(
        val selectedText: String,
        val colorHex: String = "#FFEB3B",
        val startOffset: Int = 0,
        val endOffset: Int = 0,
        val scrollProgress: Float = 0f,
        val scrollY: Int = 0
    ) : AnnotationsIntent
    data class AddNote(
        val selectedText: String,
        val note: String,
        val colorHex: String = "#81C784",
        val startOffset: Int = 0,
        val endOffset: Int = 0,
        val scrollProgress: Float = 0f,
        val scrollY: Int = 0
    ) : AnnotationsIntent
    data class Delete(val id: String) : AnnotationsIntent
    data class Export(val documentTitle: String) : AnnotationsIntent
    data object ClearExported : AnnotationsIntent
    data class SetAddBookmarkDialogOpen(val isOpen: Boolean) : AnnotationsIntent
}
