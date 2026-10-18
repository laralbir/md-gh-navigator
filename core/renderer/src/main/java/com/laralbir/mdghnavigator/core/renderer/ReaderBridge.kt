package com.laralbir.mdghnavigator.core.renderer

import android.webkit.JavascriptInterface

data class HeadingItem(
    val id: String,
    val text: String,
    val level: Int
)

/**
 * Puente JavaScript para el WebView del lector Markdown.
 * Expone métodos accesibles mediante `window.AndroidBridge` en el contexto web
 * para sincronizar scroll, clics en hipervínculos y tabla de contenidos (TOC).
 */
class ReaderBridge(
    private val onScrollProgressCallback: (progress: Float, scrollY: Int) -> Unit = { _, _ -> },
    private val onLinkClickedCallback: (url: String) -> Unit = {},
    private val onHeadingsDetectedCallback: (headings: List<HeadingItem>) -> Unit = {},
    private val onImageClickedCallback: (imageUrl: String, alt: String) -> Unit = { _, _ -> }
) {

    @JavascriptInterface
    fun onScrollProgress(progress: Float, scrollY: Int) {
        onScrollProgressCallback(progress, scrollY)
    }

    @JavascriptInterface
    fun onLinkClicked(url: String) {
        onLinkClickedCallback(url)
    }

    @JavascriptInterface
    fun onHeadingsDetected(headingsJson: String) {
        val items = parseHeadings(headingsJson)
        onHeadingsDetectedCallback(items)
    }

    @JavascriptInterface
    fun onImageClicked(url: String, alt: String = "") {
        onImageClickedCallback(url, alt)
    }

    private fun parseHeadings(json: String): List<HeadingItem> {
        val list = mutableListOf<HeadingItem>()
        val regex = Regex("""\{"id":"([^"]*)","text":"([^"]*)","level":(\d+)\}""")
        regex.findAll(json).forEach { match ->
            val (id, text, levelStr) = match.destructured
            list.add(
                HeadingItem(
                    id = id,
                    text = text,
                    level = levelStr.toIntOrNull() ?: 1
                )
            )
        }
        return list
    }
}
