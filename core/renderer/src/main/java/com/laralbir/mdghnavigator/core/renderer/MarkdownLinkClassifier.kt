package com.laralbir.mdghnavigator.core.renderer

sealed interface LinkTarget {
    data class ExternalWeb(val url: String) : LinkTarget
    data class Anchor(val anchorId: String) : LinkTarget
    data class RelativeDocument(val relativePath: String, val targetAnchor: String? = null) : LinkTarget
    data class RelativeAsset(val assetPath: String) : LinkTarget
    data class Unknown(val raw: String) : LinkTarget
}

class MarkdownLinkClassifier {

    private val assetExtensions = setOf(
        "png", "jpg", "jpeg", "gif", "svg", "webp", "ico",
        "bmp", "mp4", "webm", "pdf"
    )

    fun classify(rawLink: String): LinkTarget {
        val trimmed = rawLink.trim()
        if (trimmed.isEmpty()) return LinkTarget.Unknown(trimmed)

        // 1. In-page anchor (e.g. #architecture or #section-2)
        if (trimmed.startsWith("#")) {
            return LinkTarget.Anchor(trimmed.removePrefix("#"))
        }

        // 2. Web URL
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("mailto:", ignoreCase = true)
        ) {
            return LinkTarget.ExternalWeb(trimmed)
        }

        // 3. Separate path from hash if present (e.g. "docs/faq.md#common-questions")
        val parts = trimmed.split("#", limit = 2)
        val path = parts[0]
        val anchor = if (parts.size > 1 && parts[1].isNotEmpty()) parts[1] else null

        val extension = path.substringAfterLast('.', "").lowercase()

        // 4. Asset file vs Markdown / document file
        return if (assetExtensions.contains(extension)) {
            LinkTarget.RelativeAsset(trimmed)
        } else {
            LinkTarget.RelativeDocument(relativePath = path, targetAnchor = anchor)
        }
    }
}

