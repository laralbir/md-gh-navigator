package com.laralbir.mdghnavigator.feature.annotations

import com.laralbir.mdghnavigator.core.database.dao.AnnotationDao
import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnnotationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val annotationDao: AnnotationDao = mockk(relaxed = true)
    private val annotationsFlow = MutableStateFlow<List<AnnotationEntity>>(emptyList())
    private lateinit var viewModel: AnnotationsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { annotationDao.getAnnotationsForDocument(any()) } returns annotationsFlow
        viewModel = AnnotationsViewModel(annotationDao)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("LoadDocument collects annotations and populates list")
    fun testLoadDocument() = runTest {
        val sampleList = listOf(
            AnnotationEntity(
                id = "b1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.BOOKMARK,
                title = "Bookmark 1",
                scrollProgress = 0.2f,
                scrollY = 200
            ),
            AnnotationEntity(
                id = "h1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.HIGHLIGHT,
                selectedText = "Highlighted text",
                scrollProgress = 0.5f,
                scrollY = 500
            )
        )

        viewModel.onIntent(AnnotationsIntent.Load("doc1", "repo1"))
        annotationsFlow.value = sampleList
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.annotations.size)
        assertEquals(2, state.filteredAnnotations.size)
    }

    @Test
    @DisplayName("Filter by BOOKMARKS or HIGHLIGHTS updates filteredAnnotations")
    fun testFiltering() = runTest {
        val sampleList = listOf(
            AnnotationEntity(
                id = "b1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.BOOKMARK,
                title = "Bookmark 1",
                scrollProgress = 0.2f
            ),
            AnnotationEntity(
                id = "h1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.HIGHLIGHT,
                selectedText = "Highlighted text",
                scrollProgress = 0.5f
            )
        )
        viewModel.onIntent(AnnotationsIntent.Load("doc1", "repo1"))
        annotationsFlow.value = sampleList
        testDispatcher.scheduler.advanceUntilIdle()

        // Filter bookmarks
        viewModel.onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.BOOKMARKS))
        assertEquals(1, viewModel.uiState.value.filteredAnnotations.size)
        assertEquals(AnnotationType.BOOKMARK, viewModel.uiState.value.filteredAnnotations.first().type)

        // Filter highlights
        viewModel.onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.HIGHLIGHTS))
        assertEquals(1, viewModel.uiState.value.filteredAnnotations.size)
        assertEquals(AnnotationType.HIGHLIGHT, viewModel.uiState.value.filteredAnnotations.first().type)

        // Filter all
        viewModel.onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.ALL))
        assertEquals(2, viewModel.uiState.value.filteredAnnotations.size)
    }

    @Test
    @DisplayName("AddBookmark creates AnnotationEntity and saves via DAO")
    fun testAddBookmark() = runTest {
        viewModel.onIntent(AnnotationsIntent.Load("doc1", "repo1"))
        viewModel.onIntent(
            AnnotationsIntent.AddBookmark(
                title = "My Landmark",
                scrollProgress = 0.45f,
                scrollY = 600,
                note = "Important reference"
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            annotationDao.upsertAnnotation(
                match {
                    it.documentId == "doc1" &&
                    it.repoId == "repo1" &&
                    it.type == AnnotationType.BOOKMARK &&
                    it.title == "My Landmark" &&
                    it.scrollY == 600
                }
            )
        }
    }

    @Test
    @DisplayName("DeleteAnnotation calls DAO delete")
    fun testDeleteAnnotation() = runTest {
        viewModel.onIntent(AnnotationsIntent.Delete("b1"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { annotationDao.deleteAnnotation("b1") }
    }

    @Test
    @DisplayName("Export sets exportedMarkdown string")
    fun testExport() = runTest {
        val sampleList = listOf(
            AnnotationEntity(
                id = "b1",
                documentId = "doc1",
                repoId = "repo1",
                type = AnnotationType.BOOKMARK,
                title = "Bookmark 1",
                scrollProgress = 0.2f
            )
        )
        viewModel.onIntent(AnnotationsIntent.Load("doc1", "repo1"))
        annotationsFlow.value = sampleList
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AnnotationsIntent.Export("README"))
        assertNotNull(viewModel.uiState.value.exportedMarkdown)
        assertTrue(viewModel.uiState.value.exportedMarkdown!!.contains("README"))
    }
}
