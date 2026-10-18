package com.laralbir.mdghnavigator.feature.reader.ui

import app.cash.turbine.test
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
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
class ReaderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val documentDao: DocumentDao = mockk(relaxed = true)

    private val documentFlow = MutableStateFlow<DocumentEntity?>(null)
    private lateinit var viewModel: ReaderViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { documentDao.getDocumentById(any()) } returns documentFlow
        viewModel = ReaderViewModel(documentDao)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("LoadDocument emits document and updates lastReadAt in database")
    fun testLoadDocument() = runTest {
        val sampleDoc = DocumentEntity(
            id = "doc-1",
            repoId = "repo-1",
            filePath = "README.md",
            title = "README",
            content = "# Hola Mundo",
            scrollProgress = 0.45f,
            scrollY = 350
        )

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)

            viewModel.onIntent(ReaderIntent.LoadDocument("doc-1"))
            documentFlow.value = sampleDoc
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertEquals("doc-1", state.document?.id)
            assertEquals("# Hola Mundo", state.document?.content)
            assertEquals(0.45f, state.scrollProgress)
            assertEquals(350, state.scrollY)
            assertFalse(state.isLoading)

            coVerify(atLeast = 1) { documentDao.updateLastReadAt("doc-1", any()) }
        }
    }

    @Test
    @DisplayName("UpdateScroll updates state and persists position to Room")
    fun testUpdateScrollPersists() = runTest {
        val sampleDoc = DocumentEntity(
            id = "doc-1",
            repoId = "repo-1",
            filePath = "README.md",
            title = "README",
            content = "# Contenido",
            scrollProgress = 0.1f,
            scrollY = 50
        )
        documentFlow.value = sampleDoc
        viewModel.onIntent(ReaderIntent.LoadDocument("doc-1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(ReaderIntent.UpdateScroll(progress = 0.75f, scrollY = 820))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0.75f, viewModel.uiState.value.scrollProgress)
        assertEquals(820, viewModel.uiState.value.scrollY)

        coVerify(exactly = 1) {
            documentDao.updateScrollPosition("doc-1", 0.75f, 820)
        }
    }

    @Test
    @DisplayName("ToggleToc inverts TOC drawer visibility")
    fun testToggleToc() = runTest {
        assertFalse(viewModel.uiState.value.isTocOpen)

        viewModel.onIntent(ReaderIntent.ToggleToc)
        assertTrue(viewModel.uiState.value.isTocOpen)

        viewModel.onIntent(ReaderIntent.ToggleToc)
        assertFalse(viewModel.uiState.value.isTocOpen)
    }

    @Test
    @DisplayName("ChangeTheme and ChangeFontSize update state")
    fun testAppearanceSettings() = runTest {
        assertEquals("scholar", viewModel.uiState.value.themeMode)
        assertEquals(1.0f, viewModel.uiState.value.fontSize)

        viewModel.onIntent(ReaderIntent.ChangeTheme("neon"))
        viewModel.onIntent(ReaderIntent.ChangeFontSize(1.25f))

        assertEquals("neon", viewModel.uiState.value.themeMode)
        assertEquals(1.25f, viewModel.uiState.value.fontSize)
    }

    @Test
    @DisplayName("OpenImageViewer and CloseImageViewer toggle image modal in state")
    fun testImageViewerToggles() = runTest {
        assertNull(viewModel.uiState.value.selectedImageUrl)
        assertNull(viewModel.uiState.value.selectedImageTitle)

        viewModel.onIntent(ReaderIntent.OpenImageViewer("https://example.com/arch.png", "Architecture Diagram"))

        assertEquals("https://example.com/arch.png", viewModel.uiState.value.selectedImageUrl)
        assertEquals("Architecture Diagram", viewModel.uiState.value.selectedImageTitle)

        viewModel.onIntent(ReaderIntent.CloseImageViewer)

        assertNull(viewModel.uiState.value.selectedImageUrl)
        assertNull(viewModel.uiState.value.selectedImageTitle)
    }
}

