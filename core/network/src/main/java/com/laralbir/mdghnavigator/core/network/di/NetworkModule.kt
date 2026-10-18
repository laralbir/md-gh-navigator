package com.laralbir.mdghnavigator.core.network.di

import com.laralbir.mdghnavigator.core.common.event.EventBus
import com.laralbir.mdghnavigator.core.datastore.TokenStorage
import com.laralbir.mdghnavigator.core.network.api.GitHubApiService
import com.laralbir.mdghnavigator.core.network.interceptor.AuthInterceptor
import com.laralbir.mdghnavigator.core.network.interceptor.RateLimitInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val GITHUB_BASE_URL = "https://api.github.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Active GitHub Sectigo certificates
            .add("api.github.com", "sha256/S2LUIbq4yUg5w+MYbj5LZOWAZAzaeNGJ9rTTc4GjvBQ=")
            .add("api.github.com", "sha256/ZSagvDzjltLkewXEBuDxIzpW/dpVw1Juvvmd0hhkzdY=")
            .add("api.github.com", "sha256/sLVjNUaFYfW7n6EtgBeEpjOlcnBdNPMrZDRF36iwBdE=")
            // Legacy / backup pins
            .add("api.github.com", "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
            .add("api.github.com", "sha256/k2/oqTRJgPpeYCp+bDoglTmLioZgpdT39f42Ts8a75Y=")
            .add("api.github.com", "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenStorage: TokenStorage): AuthInterceptor {
        return AuthInterceptor(tokenStorage)
    }

    @Provides
    @Singleton
    fun provideRateLimitInterceptor(eventBus: EventBus): RateLimitInterceptor {
        return RateLimitInterceptor(eventBus)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        rateLimitInterceptor: RateLimitInterceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(rateLimitInterceptor)
            .addInterceptor(loggingInterceptor)
            .certificatePinner(certificatePinner)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }
}
