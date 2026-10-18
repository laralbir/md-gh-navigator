package com.laralbir.mdghnavigator.core.network.interceptor

import com.laralbir.mdghnavigator.core.common.event.AppEvent
import com.laralbir.mdghnavigator.core.common.event.EventBus
import com.laralbir.mdghnavigator.core.common.event.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class RateLimitInterceptor(
    private val eventBus: EventBus,
    private val warningThreshold: Int = 10,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val limit = response.header("x-ratelimit-limit")?.toIntOrNull()
        val remaining = response.header("x-ratelimit-remaining")?.toIntOrNull()
        val reset = response.header("x-ratelimit-reset")?.toLongOrNull()

        if (remaining != null && limit != null) {
            if (remaining <= warningThreshold) {
                scope.launch {
                    eventBus.publish(
                        AppEvent.LogMessage(
                            level = LogLevel.WARN,
                            tag = "RateLimit",
                            message = "GitHub API rate limit warning: $remaining remaining of $limit (resets at $reset)"
                        )
                    )
                }
            }
        }

        return response
    }
}
