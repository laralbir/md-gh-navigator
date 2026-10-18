package com.laralbir.mdghnavigator.core.common.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SelectiveFileFilterTest {

    private val filter = SelectiveFileFilter()

    @Test
    @DisplayName("Markdown files in standard paths are marked for caching")
    fun testMarkdownFiles() {
        assertTrue(filter.isMarkdown("README.md"))
        assertTrue(filter.isMarkdown("readme.MD"))
        assertTrue(filter.isMarkdown("docs/es/guia-usuario.markdown"))
        assertTrue(filter.isMarkdown("chapters/01-introduccion.mkd"))
        assertTrue(filter.shouldCache("README.md"))
    }

    @Test
    @DisplayName("Directly referenced asset images are marked for caching")
    fun testAssetFiles() {
        assertTrue(filter.isAsset("images/screenshot.png"))
        assertTrue(filter.isAsset("assets/architecture.svg"))
        assertTrue(filter.isAsset("docs/media/photo.JPG"))
        assertTrue(filter.shouldCache("images/screenshot.png"))
    }

    @Test
    @DisplayName("Code, compiled binaries, and libraries are strictly excluded from offline cache")
    fun testExcludeCodeAndBinaries() {
        assertFalse(filter.shouldCache("app-release.apk"))
        assertFalse(filter.shouldCache("libs/engine.jar"))
        assertFalse(filter.shouldCache("bin/native.so"))
        assertFalse(filter.shouldCache("src/main/java/MainActivity.kt"))
        assertFalse(filter.shouldCache("package.json"))
        assertFalse(filter.shouldCache("build.gradle.kts"))
    }

    @Test
    @DisplayName("Ignored directories (.git, node_modules, build) are excluded even if they contain markdown")
    fun testIgnoredDirectories() {
        assertFalse(filter.shouldCache("node_modules/express/README.md"))
        assertFalse(filter.shouldCache(".git/description.md"))
        assertFalse(filter.shouldCache("build/reports/tests.md"))
        assertFalse(filter.shouldCache(".idea/workspace.xml"))
    }
}
