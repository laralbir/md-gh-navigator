package com.laralbir.mdghnavigator.core.renderer

import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

import org.commonmark.node.Code
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.renderer.html.AttributeProvider
import org.commonmark.renderer.html.AttributeProviderContext
import org.commonmark.renderer.html.AttributeProviderFactory

class HtmlTemplateBuilder {

    private val extensions = listOf(
        TablesExtension.create(),
        AutolinkExtension.create()
    )

    private val parser = Parser.builder()
        .extensions(extensions)
        .build()

    private val renderer = HtmlRenderer.builder()
        .extensions(extensions)
        .attributeProviderFactory(HeadingIdAttributeProviderFactory())
        .build()

    fun renderMarkdownToHtml(markdown: String): String {
        val document = parser.parse(markdown)
        return renderer.render(document)
    }

    fun buildHtml(
        markdown: String,
        themeMode: AppThemeMode,
        isDark: Boolean,
        fontSizeSp: Int = 18,
        fontFamily: String = "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif"
    ): String {
        val (bgColor, textColor, surfaceColor, primaryColor, borderColor) = when (themeMode) {
            AppThemeMode.SCHOLAR -> if (isDark) {
                Quintet("#0F172A", "#F8FAFC", "#1E293B", "#38BDF8", "#334155")
            } else {
                Quintet("#F8FAFC", "#0F172A", "#F1F5F9", "#2563EB", "#CBD5E1")
            }
            AppThemeMode.PAPER -> if (isDark) {
                Quintet("#0D1117", "#E6EDF3", "#161B22", "#58A6FF", "#30363D")
            } else {
                Quintet("#FFFFFF", "#1F2328", "#F6F8FA", "#0969DA", "#D0D7DE")
            }
            AppThemeMode.NEON_DEV -> if (isDark) {
                Quintet("#0A0E14", "#ECEFF4", "#121721", "#00E5FF", "#3B485E")
            } else {
                Quintet("#F5F7FA", "#121721", "#FFFFFF", "#007A8A", "#6B7B94")
            }
        }

        val lineHeightPx = (fontSizeSp * 1.55).toInt()
        val renderedContent = renderMarkdownToHtml(markdown)

        return """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes">
    <link rel="stylesheet" href="https://appassets.androidplatform.net/assets/reader.css">
    <style>
        :root {
            --reader-bg: $bgColor;
            --reader-text: $textColor;
            --reader-surface: $surfaceColor;
            --reader-primary: $primaryColor;
            --reader-border: $borderColor;
            --reader-font-size: ${fontSizeSp}px;
            --reader-line-height: ${lineHeightPx}px;
            --reader-font-family: $fontFamily, sans-serif;
        }
        body {
            background-color: var(--reader-bg);
            color: var(--reader-text);
            font-family: var(--reader-font-family);
            font-size: var(--reader-font-size);
            line-height: var(--reader-line-height);
            margin: 0;
            padding: 16px 24px 80px 24px;
            word-wrap: break-word;
            overflow-wrap: break-word;
            -webkit-text-size-adjust: 100%;
        }
        pre, code {
            font-family: 'JetBrains Mono', monospace;
            background-color: var(--reader-surface);
            border-radius: 6px;
        }
        pre {
            padding: 12px;
            overflow-x: auto;
            border: 1px solid var(--reader-border);
        }
        blockquote {
            border-left: 4px solid var(--reader-primary);
            margin: 0;
            padding-left: 16px;
            color: var(--reader-text);
            opacity: 0.9;
        }
        img {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
        }
        .table-wrapper {
            width: 100%;
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
            margin: 16px 0;
        }
        table {
            border-collapse: collapse;
            width: max-content;
            min-width: 100%;
            margin: 0;
            display: table;
        }
        th, td {
            border: 1px solid var(--reader-border);
            padding: 8px 14px;
            text-align: left;
            white-space: nowrap;
        }
        th {
            background-color: var(--reader-surface);
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: rgba(128, 128, 128, 0.05);
        }
    </style>
</head>
<body data-is-dark="$isDark">
    <div id="content" class="markdown-body">$renderedContent</div>

    <script src="https://appassets.androidplatform.net/assets/mermaid.min.js"></script>
    <script src="https://appassets.androidplatform.net/assets/reader.js"></script>
    <script>
        // Report initial scroll progress
        window.addEventListener('scroll', function() {
            var h = document.documentElement, 
                b = document.body,
                st = 'scrollTop',
                sh = 'scrollHeight';
            var percent = (h[st]||b[st]) / ((h[sh]||b[sh]) - h.clientHeight);
            if (window.AndroidBridge && window.AndroidBridge.onScrollProgress) {
                window.AndroidBridge.onScrollProgress(percent, h[st]||b[st]);
            }
        });
    </script>
</body>
</html>
        """.trimIndent()
    }

    private data class Quintet(
        val bg: String,
        val text: String,
        val surface: String,
        val primary: String,
        val border: String
    )

    companion object {
        fun generateHeadingSlug(text: String): String {
            val clean = text.trim()
                .lowercase()
                .replace(Regex("[\\s_]+"), "-")
                .replace(Regex("[^\\p{L}\\p{N}-]"), "")
                .replace(Regex("-+"), "-")
                .trim('-')
            return if (clean.isEmpty()) "section" else clean
        }

        fun extractNodeText(node: Node): String {
            val sb = StringBuilder()
            var child = node.firstChild
            while (child != null) {
                when (child) {
                    is Text -> sb.append(child.literal)
                    is Code -> sb.append(child.literal)
                    else -> sb.append(extractNodeText(child))
                }
                child = child.next
            }
            return sb.toString()
        }
    }
}

class HeadingIdAttributeProviderFactory : AttributeProviderFactory {
    override fun create(context: AttributeProviderContext): AttributeProvider {
        return HeadingIdAttributeProvider()
    }
}

class HeadingIdAttributeProvider : AttributeProvider {
    private val seenSlugs = mutableMapOf<String, Int>()

    override fun setAttributes(node: Node, tagName: String, attributes: MutableMap<String, String>) {
        if (node is Heading) {
            val text = HtmlTemplateBuilder.extractNodeText(node)
            val baseSlug = HtmlTemplateBuilder.generateHeadingSlug(text)
            val count = seenSlugs.getOrDefault(baseSlug, 0)
            val uniqueSlug = if (count == 0) baseSlug else "$baseSlug-$count"
            seenSlugs[baseSlug] = count + 1
            attributes["id"] = uniqueSlug
        }
    }
}


