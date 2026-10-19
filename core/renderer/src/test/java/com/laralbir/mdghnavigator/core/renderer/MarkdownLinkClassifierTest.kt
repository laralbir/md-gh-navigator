package com.laralbir.mdghnavigator.core.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MarkdownLinkClassifierTest {

    private val classifier = MarkdownLinkClassifier()

    @Test
    @DisplayName("External web URLs are classified as ExternalWeb")
    fun testExternalWebLinks() {
        val link1 = classifier.classify("https://github.com/laralbir/md-gh-navigator")
        val link2 = classifier.classify("http://example.com/spec.html")

        assertTrue(link1 is LinkTarget.ExternalWeb)
        assertEquals("https://github.com/laralbir/md-gh-navigator", (link1 as LinkTarget.ExternalWeb).url)
        assertTrue(link2 is LinkTarget.ExternalWeb)
    }

    @Test
    @DisplayName("In-page anchors starting with # are classified as Anchor")
    fun testAnchorLinks() {
        val link = classifier.classify("#arquitectura-general")
        assertTrue(link is LinkTarget.Anchor)
        assertEquals("arquitectura-general", (link as LinkTarget.Anchor).anchorId)

        val linkUnicode = classifier.classify("#guía-de-instalación")
        assertTrue(linkUnicode is LinkTarget.Anchor)
        assertEquals("guía-de-instalación", (linkUnicode as LinkTarget.Anchor).anchorId)

        val linkEmpty = classifier.classify("#")
        assertTrue(linkEmpty is LinkTarget.Anchor)
        assertEquals("", (linkEmpty as LinkTarget.Anchor).anchorId)

        val linkVirtualAppAssetsAnchor = classifier.classify("https://appassets.androidplatform.net/#seccion")
        assertTrue(linkVirtualAppAssetsAnchor is LinkTarget.Anchor)
        assertEquals("seccion", (linkVirtualAppAssetsAnchor as LinkTarget.Anchor).anchorId)
    }

    @Test
    @DisplayName("Relative markdown files are classified as RelativeDocument")
    fun testRelativeDocumentLinks() {
        val link1 = classifier.classify("docs/es/tecnica.md")
        val link2 = classifier.classify("../installation.md#requirements")

        assertTrue(link1 is LinkTarget.RelativeDocument)
        assertEquals("docs/es/tecnica.md", (link1 as LinkTarget.RelativeDocument).relativePath)
        assertEquals(null, (link1 as LinkTarget.RelativeDocument).targetAnchor)

        assertTrue(link2 is LinkTarget.RelativeDocument)
        assertEquals("../installation.md", (link2 as LinkTarget.RelativeDocument).relativePath)
        assertEquals("requirements", (link2 as LinkTarget.RelativeDocument).targetAnchor)
    }

    @Test
    @DisplayName("Asset images are classified as RelativeAsset")
    fun testRelativeAssetLinks() {
        val link = classifier.classify("assets/architecture-diagram.png")

        assertTrue(link is LinkTarget.RelativeAsset)
        assertEquals("assets/architecture-diagram.png", (link as LinkTarget.RelativeAsset).assetPath)
    }

    @Test
    @DisplayName("appassets domain URLs from WebView clicks are resolved to RelativeDocument")
    fun testAppAssetsVirtualUrls() {
        val link = classifier.classify("https://appassets.androidplatform.net/docs/architecture.md")
        assertTrue(link is LinkTarget.RelativeDocument)
        assertEquals("docs/architecture.md", (link as LinkTarget.RelativeDocument).relativePath)
    }

    @Test
    @DisplayName("appassets asset URLs are resolved to RelativeAsset")
    fun testAppAssetsVirtualAssetUrls() {
        val link = classifier.classify("https://appassets.androidplatform.net/assets/diagram.png")
        assertTrue(link is LinkTarget.RelativeAsset)
        assertEquals("assets/diagram.png", (link as LinkTarget.RelativeAsset).assetPath)
    }

    @Test
    @DisplayName("GitHub tree directory URLs are classified as Directory")
    fun testGitHubTreeLinks() {
        val link1 = classifier.classify("https://github.com/laralbir/md-gh-navigator/tree/main/docs/architecture")
        assertTrue(link1 is LinkTarget.Directory)
        assertEquals("docs/architecture", (link1 as LinkTarget.Directory).directoryPath)

        val link2 = classifier.classify("https://github.com/owner/repo/tree/develop/guides/")
        assertTrue(link2 is LinkTarget.Directory)
        assertEquals("guides", (link2 as LinkTarget.Directory).directoryPath)
    }

    @Test
    @DisplayName("Relative directory paths with trailing slash are classified as Directory")
    fun testRelativeDirectoryLinks() {
        val link = classifier.classify("./docs/api/")
        assertTrue(link is LinkTarget.Directory)
        assertEquals("docs/api", (link as LinkTarget.Directory).directoryPath)
    }
}

