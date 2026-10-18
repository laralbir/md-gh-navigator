package com.laralbir.mdghnavigator.feature.navigator.ui

import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity

enum class NavigatorTab {
    TOC,
    FILES
}

data class NavigatorUiState(
    val repoId: String = "",
    val currentDocumentId: String? = null,
    val documents: List<DocumentEntity> = emptyList(),
    val tocItems: List<TocItemEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: NavigatorTab = NavigatorTab.TOC,
    val isLoading: Boolean = false
) {
    val filteredDocuments: List<DocumentEntity>
        get() = if (searchQuery.isBlank()) {
            documents
        } else {
            val q = searchQuery.trim().lowercase()
            documents.filter { doc ->
                doc.title.lowercase().contains(q) || doc.filePath.lowercase().contains(q)
            }
        }
}

sealed interface NavigatorIntent {
    data class UpdateSearchQuery(val query: String) : NavigatorIntent
    data class SelectTab(val tab: NavigatorTab) : NavigatorIntent
    data class SelectDocument(val document: DocumentEntity) : NavigatorIntent
    data class SelectTocItem(val tocItem: TocItemEntity) : NavigatorIntent
}
