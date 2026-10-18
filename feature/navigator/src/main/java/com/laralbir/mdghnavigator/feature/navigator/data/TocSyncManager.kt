package com.laralbir.mdghnavigator.feature.navigator.data

import com.laralbir.mdghnavigator.core.database.dao.TocDao
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TocSyncManager @Inject constructor(
    private val tocDao: TocDao
) {

    suspend fun extractAndPersistToc(documentId: String, markdown: String): List<TocItemEntity> {
        val headingRegex = Regex("""^(#{1,6})\s+(.+)$""", RegexOption.MULTILINE)
        val matches = headingRegex.findAll(markdown).toList()

        tocDao.deleteTocForDocument(documentId)

        if (matches.isEmpty()) return emptyList()

        val items = matches.mapIndexed { index, matchResult ->
            val hashes = matchResult.groupValues[1]
            val title = matchResult.groupValues[2].trim()
            val level = hashes.length
            val safeSlug = URLEncoder.encode(title.lowercase().replace("\\s+".toRegex(), "-"), "UTF-8")
            val headingId = "heading-$index-$safeSlug"

            TocItemEntity(
                id = "$documentId:$headingId",
                documentId = documentId,
                headingId = headingId,
                title = title,
                level = level,
                orderIndex = index
            )
        }

        tocDao.insertTocItems(items)
        return items
    }
}
