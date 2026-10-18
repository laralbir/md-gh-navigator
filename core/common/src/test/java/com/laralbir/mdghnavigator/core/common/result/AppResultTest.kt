package com.laralbir.mdghnavigator.core.common.result

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AppResultTest {

    @Test
    @DisplayName("AppResult.Success carries data and optional ETag")
    fun testSuccessResult() {
        val result = AppResult.Success(data = "Contenido Markdown", etag = "W/\"12345\"")

        assertTrue(result is AppResult.Success)
        assertEquals("Contenido Markdown", result.data)
        assertEquals("W/\"12345\"", result.etag)
    }

    @Test
    @DisplayName("AppResult.NotModified indicates cached content is still fresh")
    fun testNotModifiedResult() {
        val result = AppResult.NotModified(etag = "W/\"12345\"")

        assertTrue(result is AppResult.NotModified)
        assertEquals("W/\"12345\"", result.etag)
    }

    @Test
    @DisplayName("AppResult.Error preserves error message and code")
    fun testErrorResult() {
        val result = AppResult.Error(message = "Repositorio no encontrado", code = 404)

        assertTrue(result is AppResult.Error)
        assertEquals("Repositorio no encontrado", result.message)
        assertEquals(404, result.code)
    }
}

