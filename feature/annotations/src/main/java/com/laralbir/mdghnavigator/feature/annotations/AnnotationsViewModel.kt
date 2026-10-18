package com.laralbir.mdghnavigator.feature.annotations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laralbir.mdghnavigator.core.database.dao.AnnotationDao
import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AnnotationsViewModel @Inject constructor(
    private val annotationDao: AnnotationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnotationsUiState())
    val uiState: StateFlow<AnnotationsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun onIntent(intent: AnnotationsIntent) {
        when (intent) {
            is AnnotationsIntent.Load -> load(intent.documentId, intent.repoId)
            is AnnotationsIntent.SetFilter -> _uiState.update { it.copy(selectedFilter = intent.filter) }
            is AnnotationsIntent.AddBookmark -> addBookmark(intent.title, intent.scrollProgress, intent.scrollY, intent.note)
            is AnnotationsIntent.AddHighlight -> addHighlight(
                intent.selectedText,
                intent.colorHex,
                intent.startOffset,
                intent.endOffset,
                intent.scrollProgress,
                intent.scrollY
            )
            is AnnotationsIntent.AddNote -> addNote(
                intent.selectedText,
                intent.note,
                intent.colorHex,
                intent.startOffset,
                intent.endOffset,
                intent.scrollProgress,
                intent.scrollY
            )
            is AnnotationsIntent.Delete -> delete(intent.id)
            is AnnotationsIntent.Export -> export(intent.documentTitle)
            is AnnotationsIntent.ClearExported -> _uiState.update { it.copy(exportedMarkdown = null) }
            is AnnotationsIntent.SetAddBookmarkDialogOpen -> _uiState.update { it.copy(isAddBookmarkDialogOpen = intent.isOpen) }
        }
    }

    private fun load(documentId: String, repoId: String) {
        _uiState.update {
            it.copy(documentId = documentId, repoId = repoId, isLoading = true)
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            annotationDao.getAnnotationsForDocument(documentId).collect { list ->
                _uiState.update {
                    it.copy(annotations = list, isLoading = false)
                }
            }
        }
    }

    private fun addBookmark(
        title: String,
        scrollProgress: Float,
        scrollY: Int,
        note: String?
    ) {
        val currentState = _uiState.value
        val entity = AnnotationEntity(
            id = UUID.randomUUID().toString(),
            documentId = currentState.documentId,
            repoId = currentState.repoId,
            type = AnnotationType.BOOKMARK,
            title = title.ifBlank { "Marcador" },
            note = note?.ifBlank { null },
            scrollProgress = scrollProgress,
            scrollY = scrollY
        )
        viewModelScope.launch {
            annotationDao.upsertAnnotation(entity)
        }
    }

    private fun addHighlight(
        selectedText: String,
        colorHex: String,
        startOffset: Int,
        endOffset: Int,
        scrollProgress: Float,
        scrollY: Int
    ) {
        val currentState = _uiState.value
        val entity = AnnotationEntity(
            id = UUID.randomUUID().toString(),
            documentId = currentState.documentId,
            repoId = currentState.repoId,
            type = AnnotationType.HIGHLIGHT,
            selectedText = selectedText,
            colorHex = colorHex,
            startOffset = startOffset,
            endOffset = endOffset,
            scrollProgress = scrollProgress,
            scrollY = scrollY
        )
        viewModelScope.launch {
            annotationDao.upsertAnnotation(entity)
        }
    }

    private fun addNote(
        selectedText: String,
        note: String,
        colorHex: String,
        startOffset: Int,
        endOffset: Int,
        scrollProgress: Float,
        scrollY: Int
    ) {
        val currentState = _uiState.value
        val entity = AnnotationEntity(
            id = UUID.randomUUID().toString(),
            documentId = currentState.documentId,
            repoId = currentState.repoId,
            type = AnnotationType.NOTE,
            selectedText = selectedText,
            note = note,
            colorHex = colorHex,
            startOffset = startOffset,
            endOffset = endOffset,
            scrollProgress = scrollProgress,
            scrollY = scrollY
        )
        viewModelScope.launch {
            annotationDao.upsertAnnotation(entity)
        }
    }

    private fun delete(id: String) {
        viewModelScope.launch {
            annotationDao.deleteAnnotation(id)
        }
    }

    private fun export(documentTitle: String) {
        val markdown = MarkdownAnnotationExporter.export(
            documentTitle = documentTitle,
            annotations = _uiState.value.annotations
        )
        _uiState.update { it.copy(exportedMarkdown = markdown) }
    }
}
