package com.laralbir.mdghnavigator.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    fun getDocumentById(id: String): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentByIdSync(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE repoId = :repoId ORDER BY filePath ASC")
    fun getDocumentsForRepo(repoId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE lastReadTimestamp > 0 ORDER BY lastReadTimestamp DESC LIMIT :limit")
    fun getRecentlyReadDocuments(limit: Int = 10): Flow<List<DocumentEntity>>

    @Upsert
    suspend fun upsertDocument(document: DocumentEntity)

    @Query("UPDATE documents SET scrollProgress = :progress, scrollY = :scrollY, lastReadTimestamp = :timestamp WHERE id = :id")
    suspend fun updateReadingProgress(id: String, progress: Float, scrollY: Int, timestamp: Long)

    @Query("UPDATE documents SET scrollProgress = :progress, scrollY = :scrollY WHERE id = :id")
    suspend fun updateScrollPosition(id: String, progress: Float, scrollY: Int)

    @Query("UPDATE documents SET lastReadTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastReadAt(id: String, timestamp: Long)

    @Query("UPDATE documents SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM documents WHERE repoId = :repoId")
    suspend fun deleteDocumentsForRepo(repoId: String)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: String)
}
