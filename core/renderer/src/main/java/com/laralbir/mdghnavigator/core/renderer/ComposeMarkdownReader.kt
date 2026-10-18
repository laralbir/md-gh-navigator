package com.laralbir.mdghnavigator.core.renderer

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import com.laralbir.mdghnavigator.core.designsystem.theme.LocalAppThemeMode

/**
 * Componente Jetpack Compose que encapsula el lienzo de lectura Markdown.
 * Utiliza un WebView aislado con soporte de activos locales servidos mediante
 * WebViewAssetLoader (dominio https://appassets.androidplatform.net/).
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComposeMarkdownReader(
    markdown: String,
    modifier: Modifier = Modifier,
    themeMode: AppThemeMode = LocalAppThemeMode.current,
    isDark: Boolean = isSystemInDarkTheme(),
    fontSizeSp: Int = 18,
    targetScrollProgress: Float? = null,
    targetHeadingId: String? = null,
    onScrollProgress: (progress: Float, scrollY: Int) -> Unit = { _, _ -> },
    onLinkClicked: (LinkTarget) -> Unit = {},
    onHeadingsDetected: (List<HeadingItem>) -> Unit = {},
    onImageClicked: (imageUrl: String, title: String) -> Unit = { _, _ -> }
) {
    val templateBuilder = remember { HtmlTemplateBuilder() }
    val linkClassifier = remember { MarkdownLinkClassifier() }

    val htmlContent = remember(markdown, themeMode, isDark, fontSizeSp) {
        templateBuilder.buildHtml(
            markdown = markdown,
            themeMode = themeMode,
            isDark = isDark,
            fontSizeSp = fontSizeSp
        )
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                }

                val bridge = ReaderBridge(
                    onScrollProgressCallback = onScrollProgress,
                    onLinkClickedCallback = { rawUrl ->
                        val target = linkClassifier.classify(rawUrl)
                        onLinkClicked(target)
                    },
                    onHeadingsDetectedCallback = onHeadingsDetected,
                    onImageClickedCallback = onImageClicked
                )
                addJavascriptInterface(bridge, "AndroidBridge")

                webViewClient = MarkdownWebViewClient(
                    context = context,
                    onLinkTargetResolved = onLinkClicked
                )

                tag = htmlContent
                loadDataWithBaseURL(
                    "https://appassets.androidplatform.net/",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            if (webView.tag != htmlContent) {
                webView.tag = htmlContent
                webView.loadDataWithBaseURL(
                    "https://appassets.androidplatform.net/",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
            if (targetHeadingId != null) {
                webView.evaluateJavascript(
                    "window.ReaderApi && window.ReaderApi.scrollToHeading('$targetHeadingId');",
                    null
                )
            } else if (targetScrollProgress != null) {
                webView.evaluateJavascript(
                    "window.ReaderApi && window.ReaderApi.scrollToProgress($targetScrollProgress);",
                    null
                )
            }
        }
    )
}
