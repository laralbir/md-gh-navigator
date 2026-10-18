package com.laralbir.mdghnavigator.feature.reader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laralbir.mdghnavigator.core.common.util.ContentDecoder
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.feature.tts.TtsController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val documentDao: DocumentDao,
    val ttsController: TtsController? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun onIntent(intent: ReaderIntent) {
        when (intent) {
            is ReaderIntent.LoadDocument -> loadDocument(intent.documentId)
            is ReaderIntent.UpdateScroll -> updateScroll(intent.progress, intent.scrollY)
            is ReaderIntent.ToggleToc -> _uiState.update { it.copy(isTocOpen = !it.isTocOpen) }
            is ReaderIntent.SetTocOpen -> _uiState.update { it.copy(isTocOpen = intent.isOpen) }
            is ReaderIntent.ChangeTheme -> _uiState.update { it.copy(themeMode = intent.theme) }
            is ReaderIntent.ChangeFontSize -> _uiState.update { it.copy(fontSize = intent.size) }
            is ReaderIntent.OpenImageViewer -> _uiState.update {
                it.copy(
                    selectedImageUrl = intent.imageUrl,
                    selectedImageTitle = intent.title
                )
            }
            is ReaderIntent.CloseImageViewer -> _uiState.update {
                it.copy(
                    selectedImageUrl = null,
                    selectedImageTitle = null
                )
            }
        }
    }

    private fun loadDocument(documentId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            documentDao.updateLastReadAt(documentId, System.currentTimeMillis())
            documentDao.getDocumentById(documentId).collect { doc ->
                if (doc != null) {
                    val decoded = ContentDecoder.decodeIfNeeded(doc.content)
                    val effectiveDoc = if (decoded != doc.content) {
                        val fixed = doc.copy(content = decoded)
                        documentDao.upsertDocument(fixed)
                        fixed
                    } else {
                        doc
                    }
                    ttsController?.loadContent(effectiveDoc.content)
                    _uiState.update {
                        it.copy(
                            document = effectiveDoc,
                            scrollProgress = effectiveDoc.scrollProgress,
                            scrollY = effectiveDoc.scrollY,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Documento no encontrado")
                    }
                }
            }
        }
    }

    private fun updateScroll(progress: Float, scrollY: Int) {
        val currentDocId = _uiState.value.document?.id ?: return
        _uiState.update {
            it.copy(scrollProgress = progress, scrollY = scrollY)
        }
        viewModelScope.launch {
            documentDao.updateScrollPosition(currentDocId, progress, scrollY)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsController?.stop()
    }
}

