package com.laralbir.mdghnavigator.core.database.converter

import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatabaseConvertersTest {

    private val converters = DatabaseConverters()

    @Test
    @DisplayName("Converts RepositoryType enum to and from string accurately")
    fun testRepositoryTypeConversion() {
        assertEquals("GITHUB", converters.fromRepositoryType(RepositoryType.GITHUB))
        assertEquals("LOCAL_STORAGE", converters.fromRepositoryType(RepositoryType.LOCAL_STORAGE))

        assertEquals(RepositoryType.GITHUB, converters.toRepositoryType("GITHUB"))
        assertEquals(RepositoryType.LOCAL_STORAGE, converters.toRepositoryType("LOCAL_STORAGE"))
    }

    @Test
    @DisplayName("Fallback to GITHUB for unknown repository type string")
    fun testUnknownTypeFallback() {
        assertEquals(RepositoryType.GITHUB, converters.toRepositoryType("UNKNOWN_CUSTOM_TYPE"))
    }

    @Test
    @DisplayName("Converts AnnotationType enum to and from string accurately")
    fun testAnnotationTypeConversion() {
        assertEquals("BOOKMARK", converters.fromAnnotationType(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.BOOKMARK))
        assertEquals("HIGHLIGHT", converters.fromAnnotationType(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.HIGHLIGHT))
        assertEquals("NOTE", converters.fromAnnotationType(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.NOTE))

        assertEquals(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.BOOKMARK, converters.toAnnotationType("BOOKMARK"))
        assertEquals(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.HIGHLIGHT, converters.toAnnotationType("HIGHLIGHT"))
        assertEquals(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.NOTE, converters.toAnnotationType("NOTE"))
        assertEquals(com.laralbir.mdghnavigator.core.database.entity.AnnotationType.HIGHLIGHT, converters.toAnnotationType("UNKNOWN"))
    }
}
