package com.laralbir.mdghnavigator.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.designsystem.component.EmptyStateView
import com.laralbir.mdghnavigator.core.designsystem.component.MdTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onRepositoryClick: (RepositoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        val error = uiState.errorMessage
        if (error != null) {
            snackbarHostState.showSnackbar(message = error)
            viewModel.onIntent(LibraryIntent.ClearError)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MdTopAppBar(
                title = "Biblioteca",
                subtitle = "MD GitHub Navigator"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(LibraryIntent.OpenAddDialog) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir repositorio"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (uiState.repositories.isEmpty() && !uiState.isLoading) {
                EmptyStateView(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    title = "Biblioteca vacía",
                    message = "Añade tu primer repositorio de GitHub o una carpeta local para comenzar a leer tus documentos en Markdown.",
                    actionLabel = "Añadir Repositorio",
                    onActionClick = { viewModel.onIntent(LibraryIntent.OpenAddDialog) }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 340.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.repositories,
                        key = { it.id }
                    ) { repo ->
                        RepositoryCard(
                            repository = repo,
                            onClick = { onRepositoryClick(repo) },
                            onSyncClick = { viewModel.onIntent(LibraryIntent.SyncRepository(repo)) },
                            onPinClick = { viewModel.onIntent(LibraryIntent.TogglePinRepository(repo.id, repo.isPinned)) },
                            onDeleteClick = { viewModel.onIntent(LibraryIntent.DeleteRepository(repo.id)) }
                        )
                    }
                }
            }

            if (uiState.isAddDialogOpen) {
                AddRepositoryDialog(
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onDismiss = { viewModel.onIntent(LibraryIntent.CloseAddDialog) },
                    onAddGitHubRepo = { owner, repo, branch, startFile ->
                        viewModel.onIntent(
                            LibraryIntent.AddGitHubRepository(
                                owner = owner,
                                repo = repo,
                                branch = branch,
                                startFilePath = startFile
                            )
                        )
                    },
                    onSelectLocalFolder = { name, startFile ->
                        // Stub for SAF selector callback
                        viewModel.onIntent(LibraryIntent.CloseAddDialog)
                    }
                )
            }
        }
    }
}
