package com.laralbir.mdghnavigator.core.renderer

import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

class MarkdownWebViewClient(
    context: Context,
    private val onLinkTargetResolved: (LinkTarget) -> Unit,
    private val onPageFinishedCallback: ((WebView) -> Unit)? = null
) : WebViewClientCompat() {

    private val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()

    private val classifier = MarkdownLinkClassifier()

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(request.url)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean {
        val target = classifier.classify(request.url.toString())
        return when (target) {
            is LinkTarget.Anchor -> {
                // Allow anchor scrolling inside the webview
                false
            }
            else -> {
                onLinkTargetResolved(target)
                true // Handled by app
            }
        }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        onPageFinishedCallback?.invoke(view)
    }
}
