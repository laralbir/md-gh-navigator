package com.laralbir.mdghnavigator.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM repositories ORDER BY isPinned DESC, lastSyncTimestamp DESC")
    fun getAllRepositories(): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE id = :id LIMIT 1")
    fun getRepositoryById(id: String): Flow<RepositoryEntity?>

    @Query("SELECT * FROM repositories WHERE id = :id LIMIT 1")
    suspend fun getRepositoryByIdSync(id: String): RepositoryEntity?

    @Upsert
    suspend fun upsertRepository(repository: RepositoryEntity)

    @Query("DELETE FROM repositories WHERE id = :id")
    suspend fun deleteRepository(id: String)

    @Query("UPDATE repositories SET lastSyncTimestamp = :timestamp, eTag = :eTag WHERE id = :id")
    suspend fun updateSyncMetadata(id: String, timestamp: Long, eTag: String?)

    @Query("UPDATE repositories SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: String, isPinned: Boolean)

    @Query("UPDATE repositories SET alias = :alias WHERE id = :id")
    suspend fun updateAlias(id: String, alias: String?)
}
