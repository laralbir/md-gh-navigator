package com.laralbir.mdghnavigator.core.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReaderBridgeTest {

    @Test
    @DisplayName("ReaderBridge correctly dispatches scroll progress")
    fun testScrollProgress() {
        var receivedProgress = 0f
        var receivedScrollY = 0

        val bridge = ReaderBridge(
            onScrollProgressCallback = { progress, scrollY ->
                receivedProgress = progress
                receivedScrollY = scrollY
            }
        )

        bridge.onScrollProgress(0.75f, 1500)

        assertEquals(0.75f, receivedProgress)
        assertEquals(1500, receivedScrollY)
    }

    @Test
    @DisplayName("ReaderBridge correctly dispatches link clicks")
    fun testLinkClicked() {
        var clickedUrl = ""
        val bridge = ReaderBridge(onLinkClickedCallback = { clickedUrl = it })

        bridge.onLinkClicked("https://github.com/laralbir")

        assertEquals("https://github.com/laralbir", clickedUrl)
    }

    @Test
    @DisplayName("ReaderBridge parses and dispatches JSON headings list")
    fun testHeadingsParsing() {
        var headings: List<HeadingItem> = emptyList()
        val bridge = ReaderBridge(onHeadingsDetectedCallback = { headings = it })

        val sampleJson = """[{"id":"sec-1","text":"Introducción","level":1},{"id":"sec-2","text":"Instalación","level":2}]"""
        bridge.onHeadingsDetected(sampleJson)

        assertEquals(2, headings.size)
        assertEquals("sec-1", headings[0].id)
        assertEquals("Introducción", headings[0].text)
        assertEquals(1, headings[0].level)
        assertEquals("sec-2", headings[1].id)
        assertEquals("Instalación", headings[1].text)
        assertEquals(2, headings[1].level)
    }
}
