package com.laralbir.mdghnavigator.feature.tts

import java.util.regex.Pattern

/**
 * Utility for sanitizing Markdown syntax and splitting documents into natural speech sentences
 * optimized for Android Text-to-Speech (TTS) playback.
 */
object MarkdownSpeechCleaner {

    private val FENCED_CODE_REGEX = Pattern.compile("""(?s)```.*?```""")
    private val IMAGE_REGEX = Pattern.compile("""!\[.*?\]\(.*?\)""")
    private val LINK_REGEX = Pattern.compile("""\[([^]]+)]\(.*?\)""")
    private val BOLD_ITALIC_REGEX = Pattern.compile("""\*{1,3}([^*]+)\*{1,3}|_{1,3}([^_]+)_{1,3}""")
    private val INLINE_CODE_REGEX = Pattern.compile("""`([^`]+)`""")
    private val HTML_TAGS_REGEX = Pattern.compile("""<[^>]+>""")
    private val BLOCKQUOTE_REGEX = Pattern.compile("""(?m)^\s*>\s*""")
    private val HEADING_PREFIX_REGEX = Pattern.compile("""(?m)^\s*#{1,6}\s+""")

    /**
     * Cleans Markdown formatting from [markdown] and returns an ordered list of speech-ready sentences.
     */
    fun extractSpeechSentences(markdown: String): List<String> {
        if (markdown.isBlank()) return emptyList()

        // 1. Remove fenced code blocks
        var cleaned = FENCED_CODE_REGEX.matcher(markdown).replaceAll("")

        // 2. Remove images
        cleaned = IMAGE_REGEX.matcher(cleaned).replaceAll("")

        // 3. Convert links [text](url) -> text
        cleaned = LINK_REGEX.matcher(cleaned).replaceAll("$1")

        // 4. Strip bold / italic formatting
        val boldItalicMatcher = BOLD_ITALIC_REGEX.matcher(cleaned)
        val sb = StringBuffer()
        while (boldItalicMatcher.find()) {
            val text = boldItalicMatcher.group(1) ?: boldItalicMatcher.group(2) ?: ""
            boldItalicMatcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(text))
        }
        boldItalicMatcher.appendTail(sb)
        cleaned = sb.toString()

        // 5. Strip inline code `code` -> code
        cleaned = INLINE_CODE_REGEX.matcher(cleaned).replaceAll("$1")

        // 6. Strip HTML tags
        cleaned = HTML_TAGS_REGEX.matcher(cleaned).replaceAll("")

        // 7. Strip blockquote prefixes
        cleaned = BLOCKQUOTE_REGEX.matcher(cleaned).replaceAll("")

        // 8. Process line by line and split into sentences
        val sentences = mutableListOf<String>()
        val lines = cleaned.lines()

        for (rawLine in lines) {
            val line = rawLine.replace(Regex("""[ \t]+"""), " ").trim()
            if (line.isBlank()) continue

            // If line is a markdown heading
            val headingMatcher = HEADING_PREFIX_REGEX.matcher(line)
            if (headingMatcher.find()) {
                val headingTitle = headingMatcher.replaceAll("").trim()
                if (headingTitle.isNotBlank()) {
                    sentences.add(headingTitle)
                }
                continue
            }

            // Regular prose paragraph: split into sentences ending with [.!?]
            val splitSentences = splitIntoSentences(line)
            sentences.addAll(splitSentences)
        }

        return sentences.map { it.trim() }.filter { it.isNotBlank() }
    }

    private fun splitIntoSentences(text: String): List<String> {
        val result = mutableListOf<String>()
        // Match sentence up to punctuation followed by space or end of line
        val pattern = Pattern.compile("""[^.!?]+[.!?]+(?:\s+|$)""")
        val matcher = pattern.matcher(text)

        var lastEnd = 0
        while (matcher.find()) {
            result.add(matcher.group().trim())
            lastEnd = matcher.end()
        }

        if (lastEnd < text.length) {
            val remaining = text.substring(lastEnd).trim()
            if (remaining.isNotBlank()) {
                result.add(remaining)
            }
        }

        return if (result.isEmpty() && text.isNotBlank()) listOf(text.trim()) else result
    }
}
