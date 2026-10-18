package com.laralbir.mdghnavigator.feature.tts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MarkdownSpeechCleanerTest {

    @Test
    @DisplayName("Empty or whitespace input produces empty sentence list")
    fun testEmptyInput() {
        assertTrue(MarkdownSpeechCleaner.extractSpeechSentences("").isEmpty())
        assertTrue(MarkdownSpeechCleaner.extractSpeechSentences("   \n\n  ").isEmpty())
    }

    @Test
    @DisplayName("Headings are stripped of markdown hashes and treated as speech segments")
    fun testHeadings() {
        val markdown = """
            # Document Title
            This is the introductory paragraph.
            ## Subtitle Here
            Another sentence follows.
        """.trimIndent()

        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(4, sentences.size)
        assertEquals("Document Title", sentences[0])
        assertEquals("This is the introductory paragraph.", sentences[1])
        assertEquals("Subtitle Here", sentences[2])
        assertEquals("Another sentence follows.", sentences[3])
    }

    @Test
    @DisplayName("Markdown links keep text and discard URL")
    fun testLinks() {
        val markdown = "Check out the [documentation guide](https://example.com/guide) for more details."
        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(1, sentences.size)
        assertEquals("Check out the documentation guide for more details.", sentences[0])
    }

    @Test
    @DisplayName("Images are stripped from spoken sentences")
    fun testImages() {
        val markdown = "This paragraph has an image ![diagram](https://example.com/diag.png) in the middle."
        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(1, sentences.size)
        assertEquals("This paragraph has an image in the middle.", sentences[0])
    }

    @Test
    @DisplayName("Bold and italic formatting asterisks are stripped")
    fun testBoldItalic() {
        val markdown = "This is **bold text** and this is *italic text* and ***both***."
        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(1, sentences.size)
        assertEquals("This is bold text and this is italic text and both.", sentences[0])
    }

    @Test
    @DisplayName("Fenced code blocks are excluded from speech synthesis")
    fun testFencedCodeBlocks() {
        val markdown = """
            Before code block.
            ```kotlin
            fun main() {
                println("Hello world")
            }
            ```
            After code block.
        """.trimIndent()

        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(2, sentences.size)
        assertEquals("Before code block.", sentences[0])
        assertEquals("After code block.", sentences[1])
    }

    @Test
    @DisplayName("Inline code backticks are stripped preserving the code text")
    fun testInlineCode() {
        val markdown = "Run the command `git status` to verify your changes."
        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(1, sentences.size)
        assertEquals("Run the command git status to verify your changes.", sentences[0])
    }

    @Test
    @DisplayName("Sentences ending with period, exclamation, or question mark are split correctly")
    fun testSentenceSplitting() {
        val markdown = "First sentence! Is this the second? Yes, this is the third."
        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        assertEquals(3, sentences.size)
        assertEquals("First sentence!", sentences[0])
        assertEquals("Is this the second?", sentences[1])
        assertEquals("Yes, this is the third.", sentences[2])
    }
}
