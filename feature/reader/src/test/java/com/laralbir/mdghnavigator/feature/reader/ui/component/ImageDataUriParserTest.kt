package com.laralbir.mdghnavigator.feature.reader.ui.component

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.util.Base64

class ImageDataUriParserTest {

    @Test
    @DisplayName("Normal HTTP and local file URLs return unmodified string")
    fun testNormalUrlsReturnedAsIs() {
        val httpUrl = "https://example.com/image.png"
        val fileUrl = "file:///android_asset/sample.jpg"

        assertEquals(httpUrl, ImageDataUriParser.resolveImageModel(httpUrl))
        assertEquals(fileUrl, ImageDataUriParser.resolveImageModel(fileUrl))
        assertFalse(ImageDataUriParser.isSvgOrDiagram(httpUrl))
    }

    @Test
    @DisplayName("Base64 encoded SVG data URI decodes to ByteArray matching original XML")
    fun testBase64SvgDataUri() {
        val rawSvg = "<svg xmlns=\"http://www.w3.org/2000/svg\"><text>Mermaid Test</text></svg>"
        val encoded = Base64.getEncoder().encodeToString(rawSvg.toByteArray(StandardCharsets.UTF_8))
        val dataUri = "data:image/svg+xml;base64,$encoded"

        assertTrue(ImageDataUriParser.isSvgOrDiagram(dataUri))

        val result = ImageDataUriParser.resolveImageModel(dataUri)
        assertTrue(result is ByteArray, "Resolved model should be ByteArray for Coil")
        val decodedString = String(result as ByteArray, StandardCharsets.UTF_8)
        assertEquals(rawSvg, decodedString)
    }

    @Test
    @DisplayName("UTF-8 encoded SVG data URI decodes to ByteArray")
    fun testUtf8SvgDataUri() {
        val rawSvg = "<svg xmlns=\"http://www.w3.org/2000/svg\"><rect width=\"100\" height=\"100\"/></svg>"
        val dataUri = "data:image/svg+xml;utf8," + java.net.URLEncoder.encode(rawSvg, "UTF-8")

        assertTrue(ImageDataUriParser.isSvgOrDiagram(dataUri))

        val result = ImageDataUriParser.resolveImageModel(dataUri)
        assertTrue(result is ByteArray, "Resolved model should be ByteArray for Coil")
        val decodedString = String(result as ByteArray, StandardCharsets.UTF_8)
        assertEquals(rawSvg, decodedString)
    }

    @Test
    @DisplayName("isSvgOrDiagram detects .svg files and svg data URIs")
    fun testSvgDetection() {
        assertTrue(ImageDataUriParser.isSvgOrDiagram("https://github.com/logo.svg"))
        assertTrue(ImageDataUriParser.isSvgOrDiagram("file:///path/diagram.SVG"))
        assertTrue(ImageDataUriParser.isSvgOrDiagram("data:image/svg+xml;base64,PHN2Zz48L3N2Zz4="))
        assertFalse(ImageDataUriParser.isSvgOrDiagram("https://example.com/photo.png"))
        assertFalse(ImageDataUriParser.isSvgOrDiagram("https://example.com/banner.jpg"))
    }
}

