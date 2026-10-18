package com.laralbir.mdghnavigator.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Type of reading marker stored in the database.
 */
enum class AnnotationType {
    BOOKMARK,
    HIGHLIGHT,
    NOTE
}

/**
 * Entity representing an annotation, highlighted fragment, or reading bookmark in a markdown document.
 */
@Entity(
    tableName = "annotations",
    indices = [
        Index(value = ["documentId"]),
        Index(value = ["repoId"]),
        Index(value = ["type"])
    ]
)
data class AnnotationEntity(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val repoId: String,
    val type: AnnotationType = AnnotationType.HIGHLIGHT,
    val title: String? = null,
    val selectedText: String = "",
    val note: String? = null,
    val colorHex: String = "#FFEB3B",
    val startOffset: Int = 0,
    val endOffset: Int = 0,
    val scrollProgress: Float = 0f,
    val scrollY: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
