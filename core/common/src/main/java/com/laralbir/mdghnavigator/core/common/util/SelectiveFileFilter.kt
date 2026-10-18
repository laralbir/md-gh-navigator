package com.laralbir.mdghnavigator.core.common.util

/**
 * Filtro de archivos para el sistema de caché selectivo offline.
 * Garantiza que solo los documentos Markdown y los recursos gráficos vinculados
 * se descargan y persisten localmente, excluyendo código fuente, binarios y librerías
 * para optimizar el almacenamiento del dispositivo.
 */
class SelectiveFileFilter {

    private val markdownExtensions = setOf("md", "markdown", "mdown", "mkd")
    private val assetExtensions = setOf(
        "png", "jpg", "jpeg", "gif", "svg", "webp", "ico",
        "bmp", "tiff"
    )
    private val ignoredDirectories = setOf(
        ".git", ".github", ".idea", ".gradle", "node_modules",
        "build", "target", "dist", "out", "bin", "obj", "__pycache__"
    )

    fun isMarkdown(path: String): Boolean {
        if (isIgnoredPath(path)) return false
        val ext = path.substringAfterLast('.', "").lowercase()
        return markdownExtensions.contains(ext)
    }

    fun isAsset(path: String): Boolean {
        if (isIgnoredPath(path)) return false
        val ext = path.substringAfterLast('.', "").lowercase()
        return assetExtensions.contains(ext)
    }

    fun shouldCache(path: String): Boolean = isMarkdown(path) || isAsset(path)

    fun isIgnoredPath(path: String): Boolean {
        val normalized = path.replace("\\", "/")
        val segments = normalized.split("/")
        return segments.any { ignoredDirectories.contains(it) }
    }
}
