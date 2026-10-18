package com.laralbir.mdghnavigator.feature.navigator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.TocDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigatorViewModel @Inject constructor(
    private val documentDao: DocumentDao,
    private val tocDao: TocDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(NavigatorUiState())
    val uiState: StateFlow<NavigatorUiState> = _uiState.asStateFlow()

    private var docsJob: Job? = null
    private var tocJob: Job? = null

    fun loadRepository(repoId: String, initialDocId: String? = null) {
        _uiState.update {
            it.copy(
                repoId = repoId,
                currentDocumentId = initialDocId ?: it.currentDocumentId,
                isLoading = true
            )
        }

        docsJob?.cancel()
        docsJob = viewModelScope.launch {
            documentDao.getDocumentsForRepo(repoId).collect { docs ->
                _uiState.update { state ->
                    val resolvedDocId = state.currentDocumentId ?: docs.firstOrNull()?.id
                    state.copy(
                        documents = docs,
                        currentDocumentId = resolvedDocId,
                        isLoading = false
                    )
                }

                // If doc ID was updated or exists, ensure TOC is observed
                _uiState.value.currentDocumentId?.let { docId ->
                    observeToc(docId)
                }
            }
        }

        if (initialDocId != null) {
            observeToc(initialDocId)
        }
    }

    private fun observeToc(documentId: String) {
        tocJob?.cancel()
        tocJob = viewModelScope.launch {
            tocDao.getTocForDocument(documentId).collect { items ->
                _uiState.update { it.copy(tocItems = items) }
            }
        }
    }

    fun onIntent(intent: NavigatorIntent) {
        when (intent) {
            is NavigatorIntent.UpdateSearchQuery -> {
                _uiState.update { it.copy(searchQuery = intent.query) }
            }
            is NavigatorIntent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = intent.tab) }
            }
            is NavigatorIntent.SelectDocument -> {
                _uiState.update { it.copy(currentDocumentId = intent.document.id) }
                observeToc(intent.document.id)
            }
            is NavigatorIntent.SelectTocItem -> {
                // Item selection handled by consumer (e.g. scroll to anchor)
            }
        }
    }
}
