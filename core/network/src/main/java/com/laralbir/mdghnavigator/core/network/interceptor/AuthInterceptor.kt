package com.laralbir.mdghnavigator.core.network.interceptor

import com.laralbir.mdghnavigator.core.datastore.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage,
    private val tokenKey: String = "github_token"
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            tokenStorage.getToken(tokenKey)
        }

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "MD-GitHub-Navigator-Android")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
