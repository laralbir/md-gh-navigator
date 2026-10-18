package com.laralbir.mdghnavigator.core.network.interceptor

import com.laralbir.mdghnavigator.core.common.event.AppEvent
import com.laralbir.mdghnavigator.core.common.event.EventBus
import com.laralbir.mdghnavigator.core.common.event.LogLevel
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RateLimitInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var eventBus: EventBus
    private lateinit var okHttpClient: OkHttpClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        eventBus = mockk(relaxed = true)

        val rateLimitInterceptor = RateLimitInterceptor(eventBus)

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(rateLimitInterceptor)
            .build()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should notify EventBus when rate limit remaining is low`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("x-ratelimit-limit", "60")
                .setHeader("x-ratelimit-remaining", "5")
                .setHeader("x-ratelimit-reset", "1700000000")
                .setBody("{}")
        )

        val request = Request.Builder()
            .url(mockWebServer.url("/rate_limit"))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            assertEquals(200, response.code)
        }

        coVerify {
            eventBus.publish(match { event ->
                event is AppEvent.LogMessage &&
                        event.level == LogLevel.WARN &&
                        event.tag == "RateLimit" &&
                        event.message.contains("5 remaining")
            })
        }
    }
}
