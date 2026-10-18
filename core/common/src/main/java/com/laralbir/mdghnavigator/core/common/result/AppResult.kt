package com.laralbir.mdghnavigator.core.common.result

/**
 * Modelo de resultado funcional para operaciones asíncronas y de red.
 * Admite NotModified para soportar respuestas HTTP 304 basadas en ETag de GitHub.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T, val etag: String? = null) : AppResult<T>
    data class NotModified(val etag: String? = null) : AppResult<Nothing>
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val code: Int? = null
    ) : AppResult<Nothing>
}

