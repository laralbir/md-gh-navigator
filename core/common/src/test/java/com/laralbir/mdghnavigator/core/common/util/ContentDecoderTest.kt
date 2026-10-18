package com.laralbir.mdghnavigator.core.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ContentDecoderTest {

    @Test
    @DisplayName("Returns raw text unchanged if not GitHub JSON")
    fun `returns raw text unchanged if not json`() {
        val markdown = "# Guía de Usuario\n\nContenido con acentos: á, é, í, ó, ú, ñ."
        val result = ContentDecoder.decodeIfNeeded(markdown)
        assertEquals(markdown, result)
    }

    @Test
    @DisplayName("Decodes GitHub blob JSON with base64 content correctly including Spanish accents")
    fun `decodes github blob json with base64 content`() {
        val originalMarkdown = "# MD GH Navigator 🧭\n\nLector con acentos: español, cancelación, código."
        val base64Content = java.util.Base64.getEncoder().encodeToString(originalMarkdown.toByteArray(Charsets.UTF_8))
        val jsonPayload = """
            {
              "sha": "508eb1034f5904fc1921c54b2aa61ff677bfa2ad",
              "node_id": "MDQ6QmxvYjUwOGViMTAzNGY1OTA0ZmMxOTIxYzU0YjJhYTYxZmY2NzdiZmEyYWQ=",
              "size": 1234,
              "url": "https://api.github.com/repos/laralbir/md-gh-navigator/git/blobs/508eb1034f5904fc1921c54b2aa61ff677bfa2ad",
              "content": "$base64Content\n",
              "encoding": "base64"
            }
        """.trimIndent()

        val decoded = ContentDecoder.decodeIfNeeded(jsonPayload)
        assertEquals(originalMarkdown, decoded)
    }

    @Test
    @DisplayName("Decodes multi-line base64 content with newlines cleanly")
    fun `decodes multiline base64 with newlines`() {
        val text = "A".repeat(200) + " ñandú café"
        val base64WithNewlines = java.util.Base64.getMimeEncoder().encodeToString(text.toByteArray(Charsets.UTF_8))
        val jsonPayload = """{"content": "$base64WithNewlines", "encoding": "base64"}"""

        val decoded = ContentDecoder.decodeIfNeeded(jsonPayload)
        assertEquals(text, decoded)
    }

    @Test
    @DisplayName("Leaves JSON untouched if encoding is not base64")
    fun `leaves json untouched if not base64`() {
        val json = """{"content": "plain text", "encoding": "utf-8"}"""
        val result = ContentDecoder.decodeIfNeeded(json)
        assertEquals(json, result)
    }
}

