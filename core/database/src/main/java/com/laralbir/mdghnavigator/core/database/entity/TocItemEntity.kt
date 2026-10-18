package com.laralbir.mdghnavigator.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "toc_items",
    indices = [
        Index(value = ["documentId", "orderIndex"])
    ]
)
data class TocItemEntity(
    @PrimaryKey
    val id: String, // e.g. "docId:headingId"
    val documentId: String,
    val headingId: String,
    val title: String,
    val level: Int,
    val orderIndex: Int
)
