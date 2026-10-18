package com.laralbir.mdghnavigator.feature.reader.ui.component

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ZoomStateTest {

    @Test
    @DisplayName("Initial zoom state is at 1x with zero offset")
    fun testInitialState() {
        val zoom = ZoomState()
        assertEquals(1.0f, zoom.scale)
        assertEquals(0.0f, zoom.offsetX)
        assertEquals(0.0f, zoom.offsetY)
    }

    @Test
    @DisplayName("Applying zoom clamps between minScale (1.0) and maxScale (5.0)")
    fun testZoomClamping() {
        val zoom = ZoomState(minScale = 1.0f, maxScale = 5.0f)

        // Zoom out beyond 1x should stay at 1x
        zoom.applyTransform(zoomMultiplier = 0.5f, panX = 10f, panY = 10f)
        assertEquals(1.0f, zoom.scale)
        assertEquals(0.0f, zoom.offsetX)
        assertEquals(0.0f, zoom.offsetY)

        // Zoom in to 3x
        zoom.applyTransform(zoomMultiplier = 3.0f, panX = 50f, panY = -30f)
        assertEquals(3.0f, zoom.scale)
        assertEquals(50f, zoom.offsetX)
        assertEquals(-30f, zoom.offsetY)

        // Zoom beyond 5x should clamp at 5x
        zoom.applyTransform(zoomMultiplier = 3.0f, panX = 0f, panY = 0f)
        assertEquals(5.0f, zoom.scale)
    }

    @Test
    @DisplayName("Double tap toggles between 1x and 2.5x zoom")
    fun testDoubleTapToggle() {
        val zoom = ZoomState()

        // First double tap: zooms to 2.5x
        zoom.onDoubleTap()
        assertEquals(2.5f, zoom.scale)

        // Second double tap: returns to 1x
        zoom.onDoubleTap()
        assertEquals(1.0f, zoom.scale)
        assertEquals(0.0f, zoom.offsetX)
        assertEquals(0.0f, zoom.offsetY)
    }

    @Test
    @DisplayName("ZoomIn and ZoomOut step incrementally")
    fun testStepZoom() {
        val zoom = ZoomState()

        zoom.zoomIn()
        assertEquals(1.5f, zoom.scale)

        zoom.zoomIn()
        assertEquals(2.0f, zoom.scale)

        zoom.zoomOut()
        assertEquals(1.5f, zoom.scale)

        zoom.reset()
        assertEquals(1.0f, zoom.scale)
        assertEquals(0.0f, zoom.offsetX)
        assertEquals(0.0f, zoom.offsetY)
    }

    @Test
    @DisplayName("Offset is bounded when scale is at 1x")
    fun testOffsetAtOneX() {
        val zoom = ZoomState()
        zoom.applyTransform(zoomMultiplier = 1.0f, panX = 100f, panY = 100f)
        assertEquals(0.0f, zoom.offsetX)
        assertEquals(0.0f, zoom.offsetY)
    }
}

