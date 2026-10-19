package com.laralbir.mdghnavigator.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RepositoryType {
    GITHUB,
    LOCAL_STORAGE
}

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: String, // e.g. "github:owner/repo" or "local:content_hash"
    val name: String,
    val owner: String? = null,
    val type: RepositoryType,
    val defaultBranch: String = "main",
    val startFilePath: String = "README.md",
    val localUri: String? = null,
    val lastSyncTimestamp: Long = 0L,
    val eTag: String? = null,
    val isPinned: Boolean = false,
    val alias: String? = null
)
