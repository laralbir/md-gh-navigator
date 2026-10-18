package com.laralbir.mdghnavigator.feature.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import com.laralbir.mdghnavigator.feature.library.data.LocalDirectoryRepository
import com.laralbir.mdghnavigator.feature.library.data.SelectiveCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val documentDao: DocumentDao,
    private val cacheManager: SelectiveCacheManager,
    private val localRepository: LocalDirectoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observeRepositories()
    }

    fun onIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.AddGitHubRepository -> addGitHubRepository(
                owner = intent.owner,
                repo = intent.repo,
                branch = intent.branch,
                startFilePath = intent.startFilePath
            )
            is LibraryIntent.ImportLocalDirectory -> importLocalDirectory(intent)
            is LibraryIntent.SyncRepository -> syncRepository(intent.repository)
            is LibraryIntent.DeleteRepository -> deleteRepository(intent.repoId)
            is LibraryIntent.TogglePinRepository -> togglePinRepository(intent.repoId, intent.isPinned)
            LibraryIntent.OpenAddDialog -> _uiState.update { it.copy(isAddDialogOpen = true, errorMessage = null) }
            LibraryIntent.CloseAddDialog -> _uiState.update { it.copy(isAddDialogOpen = false, errorMessage = null) }
            LibraryIntent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeRepositories() {
        repositoryDao.getAllRepositories()
            .onEach { list ->
                _uiState.update { it.copy(repositories = list) }
            }
            .launchIn(viewModelScope)
    }

    private fun addGitHubRepository(
        owner: String,
        repo: String,
        branch: String?,
        startFilePath: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = cacheManager.syncGitHubRepository(owner, repo, branch, startFilePath)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isAddDialogOpen = false, errorMessage = null)
                    }
                }
                is AppResult.NotModified -> {
                    _uiState.update {
                        it.copy(isLoading = false, isAddDialogOpen = false, errorMessage = null)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun importLocalDirectory(intent: LibraryIntent.ImportLocalDirectory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = localRepository.importFiles(
                repoName = intent.name,
                uriString = intent.uriString,
                files = intent.files,
                startFilePath = intent.startFilePath
            )) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isAddDialogOpen = false, errorMessage = null)
                    }
                }
                is AppResult.NotModified -> {
                    _uiState.update {
                        it.copy(isLoading = false, isAddDialogOpen = false, errorMessage = null)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun syncRepository(repository: RepositoryEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (repository.type) {
                RepositoryType.GITHUB -> {
                    val owner = repository.owner ?: repository.id.substringAfter("github:").substringBefore("/")
                    val repo = repository.name
                    when (val result = cacheManager.syncGitHubRepository(
                        owner = owner,
                        repo = repo,
                        branch = repository.defaultBranch,
                        startFilePath = repository.startFilePath
                    )) {
                        is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        else -> _uiState.update { it.copy(isLoading = false) }
                    }
                }
                RepositoryType.LOCAL_STORAGE -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun deleteRepository(repoId: String) {
        viewModelScope.launch {
            repositoryDao.deleteRepository(repoId)
            documentDao.deleteDocumentsForRepo(repoId)
        }
    }

    private fun togglePinRepository(repoId: String, currentPinned: Boolean) {
        viewModelScope.launch {
            repositoryDao.setPinned(repoId, !currentPinned)
        }
    }
}

