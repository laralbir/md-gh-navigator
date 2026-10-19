package com.laralbir.mdghnavigator.feature.library.ui

import app.cash.turbine.test
import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import com.laralbir.mdghnavigator.feature.library.data.LocalDirectoryRepository
import com.laralbir.mdghnavigator.feature.library.data.SelectiveCacheManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repositoryDao: RepositoryDao = mockk(relaxed = true)
    private val documentDao: DocumentDao = mockk(relaxed = true)
    private val cacheManager: SelectiveCacheManager = mockk()
    private val localRepository: LocalDirectoryRepository = mockk()

    private val reposFlow = MutableStateFlow<List<RepositoryEntity>>(emptyList())

    private lateinit var viewModel: LibraryViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repositoryDao.getAllRepositories() } returns reposFlow

        viewModel = LibraryViewModel(
            repositoryDao = repositoryDao,
            documentDao = documentDao,
            cacheManager = cacheManager,
            localRepository = localRepository
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("ViewModel initially emits empty state then list when repositories exist")
    fun testInitialRepositoriesEmission() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial.repositories.isEmpty())

            val sampleRepos = listOf(
                RepositoryEntity(
                    id = "github:laralbir/md-gh-navigator",
                    name = "md-gh-navigator",
                    owner = "laralbir",
                    type = RepositoryType.GITHUB
                )
            )
            reposFlow.value = sampleRepos
            testDispatcher.scheduler.advanceUntilIdle()

            val updated = awaitItem()
            assertEquals(1, updated.repositories.size)
            assertEquals("md-gh-navigator", updated.repositories[0].name)
        }
    }

    @Test
    @DisplayName("addGitHubRepository invokes SelectiveCacheManager and closes dialog on success")
    fun testAddGitHubRepositorySuccess() = runTest {
        val repo = RepositoryEntity(
            id = "github:owner/repo",
            name = "repo",
            owner = "owner",
            type = RepositoryType.GITHUB,
            startFilePath = "README.md"
        )

        coEvery {
            cacheManager.syncGitHubRepository("owner", "repo", null, "README.md", null)
        } returns AppResult.Success(repo)

        viewModel.onIntent(LibraryIntent.OpenAddDialog)
        assertTrue(viewModel.uiState.value.isAddDialogOpen)

        viewModel.onIntent(
            LibraryIntent.AddGitHubRepository(
                owner = "owner",
                repo = "repo",
                branch = null,
                startFilePath = "README.md"
            )
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAddDialogOpen)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 1) { cacheManager.syncGitHubRepository("owner", "repo", null, "README.md", null) }
    }

    @Test
    @DisplayName("addGitHubRepository populates error message on failure")
    fun testAddGitHubRepositoryFailure() = runTest {
        coEvery {
            cacheManager.syncGitHubRepository("owner", "invalid-repo", null, "README.md", null)
        } returns AppResult.Error(message = "Repositorio no encontrado", code = 404)

        viewModel.onIntent(
            LibraryIntent.AddGitHubRepository(
                owner = "owner",
                repo = "invalid-repo"
            )
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Repositorio no encontrado", viewModel.uiState.value.errorMessage)
    }

    @Test
    @DisplayName("deleteRepository deletes repo and cached documents")
    fun testDeleteRepository() = runTest {
        viewModel.onIntent(LibraryIntent.DeleteRepository("github:owner/repo"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repositoryDao.deleteRepository("github:owner/repo") }
        coVerify(exactly = 1) { documentDao.deleteDocumentsForRepo("github:owner/repo") }
    }

    @Test
    @DisplayName("updateRepositoryAlias updates alias in repositoryDao")
    fun testUpdateRepositoryAlias() = runTest {
        viewModel.onIntent(LibraryIntent.UpdateRepositoryAlias("github:owner/repo", "Mi Alias"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repositoryDao.updateAlias("github:owner/repo", "Mi Alias") }
    }

    @Test
    @DisplayName("updateRepositoryAlias with blank clears alias in repositoryDao")
    fun testClearRepositoryAlias() = runTest {
        viewModel.onIntent(LibraryIntent.UpdateRepositoryAlias("github:owner/repo", "   "))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repositoryDao.updateAlias("github:owner/repo", null) }
    }
}

