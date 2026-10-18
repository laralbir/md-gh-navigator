package com.laralbir.mdghnavigator.feature.reader.ui

import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity

data class ReaderUiState(
    val document: DocumentEntity? = null,
    val scrollProgress: Float = 0.0f,
    val scrollY: Int = 0,
    val isLoading: Boolean = false,
    val isTocOpen: Boolean = false,
    val errorMessage: String? = null,
    val themeMode: String = "scholar",
    val fontSize: Float = 1.0f
)

sealed interface ReaderIntent {
    data class LoadDocument(val documentId: String) : ReaderIntent
    data class UpdateScroll(val progress: Float, val scrollY: Int) : ReaderIntent
    data object ToggleToc : ReaderIntent
    data class SetTocOpen(val isOpen: Boolean) : ReaderIntent
    data class ChangeTheme(val theme: String) : ReaderIntent
    data class ChangeFontSize(val size: Float) : ReaderIntent
}

