package com.laralbir.mdghnavigator.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    indices = [
        Index(value = ["repoId"]),
        Index(value = ["lastReadTimestamp"])
    ]
)
data class DocumentEntity(
    @PrimaryKey
    val id: String, // e.g. "github:owner/repo:docs/guide.md"
    val repoId: String,
    val filePath: String,
    val title: String,
    val content: String,
    val scrollProgress: Float = 0f,
    val scrollY: Int = 0,
    val lastReadTimestamp: Long = 0L,
    val fileSha: String? = null,
    val isFavorite: Boolean = false
)
