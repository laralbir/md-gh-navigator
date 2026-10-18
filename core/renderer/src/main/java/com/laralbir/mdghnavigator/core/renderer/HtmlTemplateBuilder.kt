package com.laralbir.mdghnavigator.core.renderer

import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode

class HtmlTemplateBuilder {

    fun buildHtml(
        markdown: String,
        themeMode: AppThemeMode,
        isDark: Boolean,
        fontSizeSp: Int = 18,
        fontFamily: String = "serif"
    ): String {
        val (bgColor, textColor, surfaceColor, primaryColor, borderColor) = when (themeMode) {
            AppThemeMode.SCHOLAR -> if (isDark) {
                Quintet("#1A1715", "#EDE3D8", "#24201D", "#D7CCC8", "#4F453E")
            } else {
                Quintet("#FAF7F2", "#2C2420", "#F3EDE4", "#5D4037", "#D3C4B8")
            }
            AppThemeMode.PAPER -> if (isDark) {
                Quintet("#0D1117", "#E6EDF3", "#161B22", "#E6EDF3", "#30363D")
            } else {
                Quintet("#FFFFFF", "#1F2328", "#F6F8FA", "#1F2328", "#D0D7DE")
            }
            AppThemeMode.NEON_DEV -> if (isDark) {
                Quintet("#0A0E14", "#ECEFF4", "#121721", "#00E5FF", "#3B485E")
            } else {
                Quintet("#F5F7FA", "#121721", "#FFFFFF", "#007A8A", "#6B7B94")
            }
        }

        val lineHeightPx = (fontSizeSp * 1.55).toInt()
        val escapedMarkdown = escapeForTemplate(markdown)

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
            --reader-font-family: $fontFamily, serif;
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
        table {
            border-collapse: collapse;
            width: 100%;
            margin: 16px 0;
        }
        th, td {
            border: 1px solid var(--reader-border);
            padding: 8px 12px;
            text-align: left;
        }
        th {
            background-color: var(--reader-surface);
        }
    </style>
</head>
<body>
    <div id="content"></div>
    <template id="md-source">$escapedMarkdown</template>

    <script src="https://appassets.androidplatform.net/assets/reader.js"></script>
    <script>
        // Fallback initialization if reader.js is still loading
        (function() {
            var raw = document.getElementById('md-source').innerHTML;
            var container = document.getElementById('content');
            if (typeof window.renderMarkdown === 'function') {
                window.renderMarkdown(raw);
            } else {
                // Pre-render minimal text
                container.innerHTML = '<div style="white-space: pre-wrap;">' + raw + '</div>';
            }

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
        })();
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun escapeForTemplate(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }

    private data class Quintet(
        val bg: String,
        val text: String,
        val surface: String,
        val primary: String,
        val border: String
    )
}

