package com.laralbir.mdghnavigator.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TocDao {

    @Query("SELECT * FROM toc_items WHERE documentId = :documentId ORDER BY orderIndex ASC")
    fun getTocForDocument(documentId: String): Flow<List<TocItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTocItems(items: List<TocItemEntity>)

    @Query("DELETE FROM toc_items WHERE documentId = :documentId")
    suspend fun deleteTocForDocument(documentId: String)
}
