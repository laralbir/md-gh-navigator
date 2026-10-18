package com.laralbir.mdghnavigator.feature.library.data

import com.laralbir.mdghnavigator.core.common.event.DefaultEventBus
import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.common.util.SelectiveFileFilter
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LocalDirectoryRepositoryTest {

    private val repositoryDao: RepositoryDao = mockk(relaxed = true)
    private val documentDao: DocumentDao = mockk(relaxed = true)
    private val filter = SelectiveFileFilter()
    private val eventBus = DefaultEventBus()

    private lateinit var localRepository: LocalDirectoryRepository

    @BeforeEach
    fun setUp() {
        localRepository = LocalDirectoryRepository(
            repositoryDao = repositoryDao,
            documentDao = documentDao,
            filter = filter,
            eventBus = eventBus
        )
    }

    @Test
    @DisplayName("importLocalFiles stores only markdown documents and creates local repository record")
    fun testImportLocalFiles() = runTest {
        val files = listOf(
            LocalFilePayload(relativePath = "README.md", content = "# Proyecto Local"),
            LocalFilePayload(relativePath = "manual.markdown", content = "## Guía de Uso"),
            LocalFilePayload(relativePath = "script.py", content = "print('ignore me')"),
            LocalFilePayload(relativePath = "app.apk", content = "binary")
        )

        val result = localRepository.importFiles(
            repoName = "MisDocumentos",
            uriString = "content://com.android.externalstorage.documents/tree/primary%3AMisDocumentos",
            files = files,
            startFilePath = "README.md"
        )

        assertTrue(result is AppResult.Success)
        val success = result as AppResult.Success
        assertEquals("MisDocumentos", success.data.name)
        assertEquals(RepositoryType.LOCAL_STORAGE, success.data.type)
        assertEquals("README.md", success.data.startFilePath)

        // Only 2 markdown files should be stored
        val capturedDocs = mutableListOf<DocumentEntity>()
        coVerify(exactly = 2) { documentDao.upsertDocument(capture(capturedDocs)) }

        val paths = capturedDocs.map { it.filePath }
        assertTrue(paths.contains("README.md"))
        assertTrue(paths.contains("manual.markdown"))
        assertTrue(paths.none { it.endsWith(".py") })
        assertTrue(paths.none { it.endsWith(".apk") })
    }

    @Test
    @DisplayName("importLocalFiles selects fallback start file if requested start file is missing")
    fun testFallbackStartFile() = runTest {
        val files = listOf(
            LocalFilePayload(relativePath = "capitulo1.md", content = "# Primer Capítulo")
        )

        val result = localRepository.importFiles(
            repoName = "Novela",
            uriString = "content://tree/novela",
            files = files,
            startFilePath = "README.md"
        )

        assertTrue(result is AppResult.Success)
        val success = result as AppResult.Success
        assertEquals("capitulo1.md", success.data.startFilePath)
    }
}
