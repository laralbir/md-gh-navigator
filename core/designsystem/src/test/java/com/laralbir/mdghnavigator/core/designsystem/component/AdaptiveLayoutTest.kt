package com.laralbir.mdghnavigator.core.designsystem.component

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AdaptiveLayoutTest {

    @Test
    @DisplayName("Tablet TCL NXTPAPER 11 Plus in landscape (1100+ dp) selects two-pane split layout")
    fun testTclTabletLandscape() {
        // TCL NXTPAPER 11 Plus is 2200x1440 px at approx 240-280 dpi (~1100dp x 720dp)
        val layout = computeReaderWindowLayout(screenWidthDp = 1100)

        assertTrue(layout.isTwoPaneSplitEnabled, "Split view should be enabled for 1100dp")
        assertTrue(layout.isTabletOrDesktop, "Should detect tablet screen size")
        assertEquals(LayoutType.EXPANDED, layout.type)
    }

    @Test
    @DisplayName("Smartphone in portrait (360 - 412 dp) selects compact single-pane layout")
    fun testSmartphonePortrait() {
        val layout = computeReaderWindowLayout(screenWidthDp = 390)

        assertFalse(layout.isTwoPaneSplitEnabled, "Split view must be disabled for phones in portrait")
        assertFalse(layout.isTabletOrDesktop)
        assertEquals(LayoutType.COMPACT, layout.type)
    }

    @Test
    @DisplayName("Tablet in portrait (720 - 800 dp) selects medium layout with navigation rail")
    fun testTabletPortrait() {
        val layout = computeReaderWindowLayout(screenWidthDp = 720)

        assertFalse(layout.isTwoPaneSplitEnabled, "Medium width uses drawer/rail rather than fixed two-pane split")
        assertTrue(layout.isTabletOrDesktop)
        assertEquals(LayoutType.MEDIUM, layout.type)
    }
}
