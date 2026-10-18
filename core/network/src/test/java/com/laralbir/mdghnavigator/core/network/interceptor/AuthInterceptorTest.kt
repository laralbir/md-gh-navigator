package com.laralbir.mdghnavigator.core.network.interceptor

import com.laralbir.mdghnavigator.core.datastore.TokenStorage
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var tokenStorage: TokenStorage
    private lateinit var okHttpClient: OkHttpClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        tokenStorage = mockk()

        val authInterceptor = AuthInterceptor(tokenStorage)

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should attach Authorization Bearer header when token exists`() {
        coEvery { tokenStorage.getToken("github_token") } returns "ghp_validToken123"

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val request = Request.Builder()
            .url(mockWebServer.url("/user"))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            assertEquals(200, response.code)
        }

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer ghp_validToken123", recordedRequest.getHeader("Authorization"))
        assertEquals("application/vnd.github.v3+json", recordedRequest.getHeader("Accept"))
        assertEquals("MD-GitHub-Navigator-Android", recordedRequest.getHeader("User-Agent"))
    }

    @Test
    fun `should not attach Authorization header when token is null`() {
        coEvery { tokenStorage.getToken("github_token") } returns null

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val request = Request.Builder()
            .url(mockWebServer.url("/user"))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            assertEquals(200, response.code)
        }

        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
        assertEquals("application/vnd.github.v3+json", recordedRequest.getHeader("Accept"))
    }
}
