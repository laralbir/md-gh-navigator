package com.laralbir.mdghnavigator.core.common.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class GitHubBlobPayload(
    val content: String? = null,
    val encoding: String? = null
)

object ContentDecoder {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Inspects raw document content. If it detects a GitHub API JSON response
     * (e.g. `{"sha": "...", "content": "base64...", "encoding": "base64"}`),
     * it decodes the base64 content into UTF-8 Markdown text.
     * Otherwise, returns the input string as-is.
     */
    fun decodeIfNeeded(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.startsWith("{") && trimmed.contains("\"content\"") && trimmed.contains("\"encoding\"")) {
            try {
                val payload = json.decodeFromString<GitHubBlobPayload>(trimmed)
                if (payload.encoding.equals("base64", ignoreCase = true) && !payload.content.isNullOrBlank()) {
                    return decodeBase64(payload.content)
                }
            } catch (_: Exception) {
                // Not a valid JSON payload or format differs; fall back to returning raw
            }
        }
        return raw
    }

    /**
     * Safely decodes a Base64 string to UTF-8 text across both Android (API 24+) and JVM runtime.
     * Strips whitespace and newlines that GitHub's API inserts every 60 characters.
     */
    fun decodeBase64(base64Str: String): String {
        val clean = base64Str.replace("\n", "").replace("\r", "").trim()
        val bytes = try {
            // JVM fallback or Android API 26+
            java.util.Base64.getDecoder().decode(clean)
        } catch (_: Throwable) {
            // Android runtime fallback for older APIs if needed
            android.util.Base64.decode(clean, android.util.Base64.DEFAULT)
        }
        return String(bytes, Charsets.UTF_8)
    }
}

