package com.laralbir.mdghnavigator.feature.library.ui

import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.feature.library.data.LocalFilePayload

data class LibraryUiState(
    val isLoading: Boolean = false,
    val repositories: List<RepositoryEntity> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LibraryIntent {
    data class AddGitHubRepository(
        val owner: String,
        val repo: String,
        val branch: String? = null,
        val startFilePath: String = "README.md"
    ) : LibraryIntent

    data class ImportLocalDirectory(
        val name: String,
        val uriString: String,
        val files: List<LocalFilePayload>,
        val startFilePath: String = "README.md"
    ) : LibraryIntent

    data class SyncRepository(val repository: RepositoryEntity) : LibraryIntent
    data class DeleteRepository(val repoId: String) : LibraryIntent
    data class TogglePinRepository(val repoId: String, val isPinned: Boolean) : LibraryIntent
    data object OpenAddDialog : LibraryIntent
    data object CloseAddDialog : LibraryIntent
    data object ClearError : LibraryIntent
}

