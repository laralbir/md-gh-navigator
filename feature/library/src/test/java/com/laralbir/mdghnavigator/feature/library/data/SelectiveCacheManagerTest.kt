package com.laralbir.mdghnavigator.feature.library.data

import com.laralbir.mdghnavigator.core.common.event.DefaultEventBus
import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.common.util.SelectiveFileFilter
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import com.laralbir.mdghnavigator.core.network.datasource.GitHubRemoteDataSource
import com.laralbir.mdghnavigator.core.network.model.GitHubRepoDto
import com.laralbir.mdghnavigator.core.network.model.GitHubTreeDto
import com.laralbir.mdghnavigator.core.network.model.GitHubTreeNodeDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SelectiveCacheManagerTest {

    private val remoteDataSource: GitHubRemoteDataSource = mockk()
    private val repositoryDao: RepositoryDao = mockk(relaxed = true)
    private val documentDao: DocumentDao = mockk(relaxed = true)
    private val filter = SelectiveFileFilter()
    private val eventBus = DefaultEventBus()

    private lateinit var cacheManager: SelectiveCacheManager

    @BeforeEach
    fun setUp() {
        cacheManager = SelectiveCacheManager(
            remoteDataSource = remoteDataSource,
            repositoryDao = repositoryDao,
            documentDao = documentDao,
            filter = filter,
            eventBus = eventBus
        )
    }

    @Test
    @DisplayName("syncGitHubRepository selectively caches only Markdown documents and ignores code/binaries")
    fun testSelectiveCaching() = runTest {
        val repoDto = GitHubRepoDto(
            id = 101,
            name = "my-docs",
            fullName = "owner/my-docs",
            defaultBranch = "main"
        )

        val treeNodes = listOf(
            GitHubTreeNodeDto(path = "README.md", mode = "100644", type = "blob", sha = "sha1", url = "url1"),
            GitHubTreeNodeDto(path = "docs/chapter1.markdown", mode = "100644", type = "blob", sha = "sha2", url = "url2"),
            GitHubTreeNodeDto(path = "src/Main.kt", mode = "100644", type = "blob", sha = "sha3", url = "url3"),
            GitHubTreeNodeDto(path = "bin/output.jar", mode = "100644", type = "blob", sha = "sha4", url = "url4"),
            GitHubTreeNodeDto(path = "node_modules/pkg/readme.md", mode = "100644", type = "blob", sha = "sha5", url = "url5")
        )

        coEvery { remoteDataSource.fetchRepository("owner", "my-docs", any()) } returns AppResult.Success(repoDto, "etag-1")
        coEvery { remoteDataSource.fetchTree("owner", "my-docs", "main", any()) } returns AppResult.Success(
            GitHubTreeDto(sha = "treesha", url = "treeurl", tree = treeNodes), "etag-tree"
        )
        coEvery { remoteDataSource.fetchRawContent("url1", any()) } returns AppResult.Success("# Readme Principal")
        coEvery { remoteDataSource.fetchRawContent("url2", any()) } returns AppResult.Success("## Capitulo 1")
        coEvery { repositoryDao.getRepositoryByIdSync("github:owner/my-docs") } returns null
        coEvery { documentDao.getDocumentByIdSync(any()) } returns null

        val result = cacheManager.syncGitHubRepository("owner", "my-docs")

        assertTrue(result is AppResult.Success)
        val success = result as AppResult.Success
        assertEquals("github:owner/my-docs", success.data.id)
        assertEquals("README.md", success.data.startFilePath)

        // Verify only README.md and chapter1.markdown were saved to Room
        val savedDocs = mutableListOf<DocumentEntity>()
        coVerify(exactly = 2) { documentDao.upsertDocument(capture(savedDocs)) }

        val savedPaths = savedDocs.map { it.filePath }
        assertTrue(savedPaths.contains("README.md"))
        assertTrue(savedPaths.contains("docs/chapter1.markdown"))
        // Code and binaries must NOT be saved
        assertTrue(savedPaths.none { it.endsWith(".kt") })
        assertTrue(savedPaths.none { it.endsWith(".jar") })
        assertTrue(savedPaths.none { it.contains("node_modules") })
    }

    @Test
    @DisplayName("syncGitHubRepository handles HTTP 304 Not Modified without rewriting cache")
    fun testNotModifiedHandlesGracefully() = runTest {
        val existingRepo = RepositoryEntity(
            id = "github:owner/fresh-repo",
            name = "fresh-repo",
            owner = "owner",
            type = RepositoryType.GITHUB,
            eTag = "cached-etag-99"
        )

        coEvery { repositoryDao.getRepositoryByIdSync("github:owner/fresh-repo") } returns existingRepo
        coEvery { remoteDataSource.fetchRepository("owner", "fresh-repo", "cached-etag-99") } returns AppResult.NotModified("cached-etag-99")

        val result = cacheManager.syncGitHubRepository("owner", "fresh-repo")

        assertTrue(result is AppResult.NotModified)
        // No documents should be fetched or rewritten
        coVerify(exactly = 0) { remoteDataSource.fetchTree(any(), any(), any(), any()) }
        coVerify(exactly = 0) { documentDao.upsertDocument(any()) }
    }

    @Test
    @DisplayName("syncGitHubRepository preserves user's reading progress during sync")
    fun testPreserveReadingProgress() = runTest {
        val repoDto = GitHubRepoDto(
            id = 101,
            name = "book",
            fullName = "author/book",
            defaultBranch = "main"
        )
        val treeNodes = listOf(
            GitHubTreeNodeDto(path = "README.md", mode = "100644", type = "blob", sha = "new-sha", url = "url1")
        )

        val existingDoc = DocumentEntity(
            id = "github:author/book:README.md",
            repoId = "github:author/book",
            filePath = "README.md",
            title = "Book",
            content = "Old content",
            scrollProgress = 0.65f,
            scrollY = 1200,
            lastReadTimestamp = 999999L
        )

        coEvery { remoteDataSource.fetchRepository("author", "book", any()) } returns AppResult.Success(repoDto, "etag-1")
        coEvery { remoteDataSource.fetchTree("author", "book", "main", any()) } returns AppResult.Success(
            GitHubTreeDto(sha = "treesha", url = "treeurl", tree = treeNodes), "etag-tree"
        )
        coEvery { remoteDataSource.fetchRawContent("url1", any()) } returns AppResult.Success("# New Updated Content")
        coEvery { repositoryDao.getRepositoryByIdSync("github:author/book") } returns null
        coEvery { documentDao.getDocumentByIdSync("github:author/book:README.md") } returns existingDoc

        val capturedDoc = slot<DocumentEntity>()
        coEvery { documentDao.upsertDocument(capture(capturedDoc)) } returns Unit

        val result = cacheManager.syncGitHubRepository("author", "book")

        assertTrue(result is AppResult.Success)
        assertEquals(0.65f, capturedDoc.captured.scrollProgress, "Must preserve user's reading scroll progress")
        assertEquals(1200, capturedDoc.captured.scrollY, "Must preserve user's scrollY")
        assertEquals("# New Updated Content", capturedDoc.captured.content)
    }
}
