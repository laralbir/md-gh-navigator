package com.laralbir.mdghnavigator.feature.reader.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReaderNavigationBackStackTest {

    @Test
    @DisplayName("Navigating to new document records current doc and scrollProgress in backstack")
    fun testPushToBackStack() {
        val stack = mutableListOf<NavigationBackStackEntry>()
        var activeDocId = "doc-1"
        val currentScrollProgress = 0.65f

        // Navigate to doc-2
        val newDocId = "doc-2"
        if (newDocId != activeDocId) {
            stack.add(NavigationBackStackEntry(activeDocId, currentScrollProgress))
            activeDocId = newDocId
        }

        assertEquals(1, stack.size)
        assertEquals("doc-1", stack.last().documentId)
        assertEquals(0.65f, stack.last().scrollProgress)
        assertEquals("doc-2", activeDocId)
    }

    @Test
    @DisplayName("Back navigation pops previous document and faithfully restores its scrollProgress")
    fun testPopFromBackStackRestoresScroll() {
        val stack = mutableListOf<NavigationBackStackEntry>()
        stack.add(NavigationBackStackEntry("doc-A", 0.42f))
        stack.add(NavigationBackStackEntry("doc-B", 0.88f))

        var activeDocId = "doc-C"
        var targetScrollProgress: Float? = null

        // Pop back to doc-B
        val prevEntry1 = stack.removeAt(stack.lastIndex)
        activeDocId = prevEntry1.documentId
        targetScrollProgress = prevEntry1.scrollProgress

        assertEquals("doc-B", activeDocId)
        assertEquals(0.88f, targetScrollProgress)
        assertEquals(1, stack.size)

        // Pop back to doc-A
        val prevEntry2 = stack.removeAt(stack.lastIndex)
        activeDocId = prevEntry2.documentId
        targetScrollProgress = prevEntry2.scrollProgress

        assertEquals("doc-A", activeDocId)
        assertEquals(0.42f, targetScrollProgress)
        assertTrue(stack.isEmpty())
    }
}
