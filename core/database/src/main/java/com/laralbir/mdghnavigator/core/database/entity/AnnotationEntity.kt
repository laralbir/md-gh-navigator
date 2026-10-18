package com.laralbir.mdghnavigator.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "annotations",
    indices = [
        Index(value = ["documentId"]),
        Index(value = ["repoId"])
    ]
)
data class AnnotationEntity(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val repoId: String,
    val selectedText: String,
    val note: String? = null,
    val colorHex: String = "#FFEB3B",
    val startOffset: Int = 0,
    val endOffset: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
