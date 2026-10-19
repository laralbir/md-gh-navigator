package com.laralbir.mdghnavigator.feature.library.data

import com.laralbir.mdghnavigator.core.common.event.AppEvent
import com.laralbir.mdghnavigator.core.common.event.EventBus
import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.common.util.ContentDecoder
import com.laralbir.mdghnavigator.core.common.util.SelectiveFileFilter
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import com.laralbir.mdghnavigator.core.network.datasource.GitHubRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectiveCacheManager @Inject constructor(
    private val remoteDataSource: GitHubRemoteDataSource,
    private val repositoryDao: RepositoryDao,
    private val documentDao: DocumentDao,
    private val filter: SelectiveFileFilter,
    private val eventBus: EventBus
) {

    suspend fun syncGitHubRepository(
        owner: String,
        repo: String,
        branch: String? = null,
        startFilePath: String = "README.md"
    ): AppResult<RepositoryEntity> {
        val repoId = "github:$owner/$repo"
        val existingRepo = repositoryDao.getRepositoryByIdSync(repoId)

        // 1. Fetch Repository metadata with ETag support
        val repoResult = remoteDataSource.fetchRepository(
            owner = owner,
            repo = repo,
            etag = existingRepo?.eTag
        )

        val (repoDto, newEtag) = when (repoResult) {
            is AppResult.NotModified -> return AppResult.NotModified(repoResult.etag)
            is AppResult.Error -> return repoResult
            is AppResult.Success -> Pair(repoResult.data, repoResult.etag)
        }

        val targetBranch = branch ?: repoDto.defaultBranch

        // 2. Fetch Git Tree recursively
        val treeResult = remoteDataSource.fetchTree(
            owner = owner,
            repo = repo,
            treeSha = targetBranch
        )

        val treeDto = when (treeResult) {
            is AppResult.NotModified -> return AppResult.NotModified(treeResult.etag)
            is AppResult.Error -> return treeResult
            is AppResult.Success -> treeResult.data
        }

        // 3. Filter tree nodes: only Markdown files and direct documentation assets
        val markdownNodes = treeDto.tree.filter { node ->
            node.type == "blob" && filter.isMarkdown(node.path)
        }

        val cachedPaths = mutableListOf<String>()

        for (node in markdownNodes) {
            val contentResult = remoteDataSource.fetchRawContent(node.url)
            if (contentResult is AppResult.Success) {
                val decodedContent = ContentDecoder.decodeIfNeeded(contentResult.data)
                val docId = "$repoId:${node.path}"
                val existingDoc = documentDao.getDocumentByIdSync(docId)

                val document = DocumentEntity(
                    id = docId,
                    repoId = repoId,
                    filePath = node.path,
                    title = extractTitle(decodedContent, node.path),
                    content = decodedContent,
                    scrollProgress = existingDoc?.scrollProgress ?: 0f,
                    scrollY = existingDoc?.scrollY ?: 0,
                    lastReadTimestamp = existingDoc?.lastReadTimestamp ?: 0L,
                    fileSha = node.sha
                )

                documentDao.upsertDocument(document)
                cachedPaths.add(node.path)
            }
        }

        // 4. Resolve starting file path
        val resolvedStartFile = resolveStartFile(cachedPaths, startFilePath)

        val updatedRepo = RepositoryEntity(
            id = repoId,
            name = repoDto.name,
            owner = owner,
            type = RepositoryType.GITHUB,
            defaultBranch = targetBranch,
            startFilePath = resolvedStartFile,
            lastSyncTimestamp = System.currentTimeMillis(),
            eTag = newEtag,
            isPinned = existingRepo?.isPinned ?: false
        )

        repositoryDao.upsertRepository(updatedRepo)
        eventBus.publish(AppEvent.FileChanged(sourceId = repoId, filePath = resolvedStartFile))

        return AppResult.Success(updatedRepo, newEtag)
    }

    private fun resolveStartFile(cachedPaths: List<String>, requested: String): String {
        if (cachedPaths.contains(requested)) return requested
        val fallbackNames = listOf("README.md", "readme.md", "Readme.md", "index.md", "Index.md")
        for (fallback in fallbackNames) {
            if (cachedPaths.contains(fallback)) return fallback
        }
        return cachedPaths.firstOrNull() ?: requested
    }

    private fun extractTitle(content: String, path: String): String {
        val h1Line = content.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.startsWith("# ") }

        return if (h1Line != null) {
            h1Line.removePrefix("# ").trim()
        } else {
            path.substringAfterLast('/').substringBeforeLast('.')
        }
    }
}
