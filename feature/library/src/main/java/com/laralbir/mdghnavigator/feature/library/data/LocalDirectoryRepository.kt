package com.laralbir.mdghnavigator.feature.library.data

import com.laralbir.mdghnavigator.core.common.event.AppEvent
import com.laralbir.mdghnavigator.core.common.event.EventBus
import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.common.util.SelectiveFileFilter
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

data class LocalFilePayload(
    val relativePath: String,
    val content: String,
    val lastModified: Long = System.currentTimeMillis()
)

/**
 * Repositorio de contenidos locales que integra el Storage Access Framework (SAF) de Android.
 * Permite seleccionar carpetas del almacenamiento del dispositivo, filtrar selectivamente
 * documentos Markdown y persistirlos en la base de datos local para lectura desconectada.
 */
@Singleton
class LocalDirectoryRepository @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val documentDao: DocumentDao,
    private val filter: SelectiveFileFilter,
    private val eventBus: EventBus
) {

    suspend fun importFiles(
        repoName: String,
        uriString: String,
        files: List<LocalFilePayload>,
        startFilePath: String = "README.md"
    ): AppResult<RepositoryEntity> {
        val repoHash = computeSha256(uriString).take(12)
        val repoId = "local:$repoHash"
        val existingRepo = repositoryDao.getRepositoryByIdSync(repoId)

        val markdownFiles = files.filter { filter.isMarkdown(it.relativePath) }
        val cachedPaths = mutableListOf<String>()

        for (file in markdownFiles) {
            val docId = "$repoId:${file.relativePath}"
            val existingDoc = documentDao.getDocumentByIdSync(docId)

            val document = DocumentEntity(
                id = docId,
                repoId = repoId,
                filePath = file.relativePath,
                title = extractTitle(file.content, file.relativePath),
                content = file.content,
                scrollProgress = existingDoc?.scrollProgress ?: 0f,
                scrollY = existingDoc?.scrollY ?: 0,
                lastReadTimestamp = existingDoc?.lastReadTimestamp ?: 0L,
                fileSha = file.lastModified.toString()
            )

            documentDao.upsertDocument(document)
            cachedPaths.add(file.relativePath)
        }

        val resolvedStartFile = resolveStartFile(cachedPaths, startFilePath)

        val repository = RepositoryEntity(
            id = repoId,
            name = repoName,
            type = RepositoryType.LOCAL_STORAGE,
            localUri = uriString,
            startFilePath = resolvedStartFile,
            lastSyncTimestamp = System.currentTimeMillis(),
            isPinned = existingRepo?.isPinned ?: false
        )

        repositoryDao.upsertRepository(repository)
        eventBus.publish(AppEvent.FileChanged(sourceId = repoId, filePath = resolvedStartFile))

        return AppResult.Success(repository)
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

    private fun computeSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
