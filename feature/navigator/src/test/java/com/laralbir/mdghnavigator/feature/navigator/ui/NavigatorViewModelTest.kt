package com.laralbir.mdghnavigator.feature.navigator.ui

import app.cash.turbine.test
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.TocDao
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity
import io.mockk.coEvery
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val documentDao: DocumentDao = mockk(relaxed = true)
    private val tocDao: TocDao = mockk(relaxed = true)

    private val documentsFlow = MutableStateFlow<List<DocumentEntity>>(emptyList())
    private val tocFlow = MutableStateFlow<List<TocItemEntity>>(emptyList())

    private lateinit var viewModel: NavigatorViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { documentDao.getDocumentsForRepo(any()) } returns documentsFlow
        coEvery { tocDao.getTocForDocument(any()) } returns tocFlow

        viewModel = NavigatorViewModel(documentDao, tocDao)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("NavigatorViewModel initializes with documents and TOC streams")
    fun testInitialization() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial.documents.isEmpty())

            viewModel.loadRepository("repo-101", "doc-1")

            val docs = listOf(
                DocumentEntity(id = "doc-1", repoId = "repo-101", filePath = "README.md", title = "Readme", content = "")
            )
            val tocs = listOf(
                TocItemEntity(id = "t1", documentId = "doc-1", headingId = "h1", title = "Intro", level = 1, orderIndex = 0)
            )

            documentsFlow.value = docs
            tocFlow.value = tocs

            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertEquals(1, state.documents.size)
            assertEquals(1, state.tocItems.size)
            assertEquals("README.md", state.documents[0].filePath)
            assertEquals("Intro", state.tocItems[0].title)
        }
    }

    @Test
    @DisplayName("Search query filters documents list correctly")
    fun testSearchFiltering() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.loadRepository("repo-101", "doc-1")
            documentsFlow.value = listOf(
                DocumentEntity(id = "doc-1", repoId = "repo-101", filePath = "README.md", title = "Readme Principal", content = ""),
                DocumentEntity(id = "doc-2", repoId = "repo-101", filePath = "docs/guia.md", title = "Guía Técnica", content = "")
            )
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onIntent(NavigatorIntent.UpdateSearchQuery("Guía"))
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            val filtered = state.filteredDocuments
            assertEquals(1, filtered.size)
            assertEquals("Guía Técnica", filtered[0].title)
        }
    }

    @Test
    @DisplayName("Tab selection updates selectedTab in UI state")
    fun testTabSelection() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(NavigatorTab.TOC, initial.selectedTab)

            viewModel.onIntent(NavigatorIntent.SelectTab(NavigatorTab.FILES))
            testDispatcher.scheduler.advanceUntilIdle()

            val updated = expectMostRecentItem()
            assertEquals(NavigatorTab.FILES, updated.selectedTab)
        }
    }
}
