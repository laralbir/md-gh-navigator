package com.laralbir.mdghnavigator.feature.navigator.data

import com.laralbir.mdghnavigator.core.database.dao.TocDao
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TocSyncManagerTest {

    private val tocDao: TocDao = mockk(relaxed = true)
    private lateinit var syncManager: TocSyncManager

    @BeforeEach
    fun setUp() {
        syncManager = TocSyncManager(tocDao)
    }

    @Test
    @DisplayName("extractAndPersistToc extracts headings hierarchy and saves to TocDao")
    fun testExtractHeadings() = runTest {
        val markdown = """
            # Manual de Usuario
            Bienvenido al lector.
            
            ## 1. Primeros Pasos
            Contenido de primeros pasos.
            
            ### 1.1 Conectar GitHub
            Configurar token.
            
            ## 2. Lectura Offline
            Descarga selectiva.
        """.trimIndent()

        val capturedList = slot<List<TocItemEntity>>()
        coEvery { tocDao.insertTocItems(capture(capturedList)) } returns Unit

        val items = syncManager.extractAndPersistToc("doc-101", markdown)

        assertEquals(4, items.size)
        assertEquals("Manual de Usuario", items[0].title)
        assertEquals(1, items[0].level)
        assertEquals(0, items[0].orderIndex)

        assertEquals("1. Primeros Pasos", items[1].title)
        assertEquals(2, items[1].level)

        assertEquals("1.1 Conectar GitHub", items[2].title)
        assertEquals(3, items[2].level)

        assertEquals("2. Lectura Offline", items[3].title)
        assertEquals(2, items[3].level)

        coVerify(exactly = 1) { tocDao.deleteTocForDocument("doc-101") }
        coVerify(exactly = 1) { tocDao.insertTocItems(any()) }
    }

    @Test
    @DisplayName("extractAndPersistToc returns empty list when document has no headings")
    fun testEmptyHeadings() = runTest {
        val markdown = "Solo texto plano sin ningun encabezado."
        val items = syncManager.extractAndPersistToc("doc-plain", markdown)

        assertEquals(0, items.size)
        coVerify(exactly = 1) { tocDao.deleteTocForDocument("doc-plain") }
        coVerify(exactly = 0) { tocDao.insertTocItems(any()) }
    }
}
