package com.laralbir.mdghnavigator.core.network.datasource

import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.network.api.GitHubApiService
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

class GitHubRemoteDataSourceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: GitHubApiService
    private lateinit var dataSource: GitHubRemoteDataSource

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(GitHubApiService::class.java)
        dataSource = GitHubRemoteDataSource(apiService)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    @DisplayName("fetchRepository returns Success with ETag on 200 OK")
    fun testFetchRepositorySuccess() = runTest {
        val body = """
            {
                "id": 12345,
                "name": "md-gh-navigator",
                "full_name": "laralbir/md-gh-navigator",
                "default_branch": "main"
            }
        """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("ETag", "W/\"repo-etag-1\"")
                .setBody(body)
        )

        val result = dataSource.fetchRepository("laralbir", "md-gh-navigator")

        assertTrue(result is AppResult.Success)
        val success = result as AppResult.Success
        assertEquals("md-gh-navigator", success.data.name)
        assertEquals("W/\"repo-etag-1\"", success.etag)
    }

    @Test
    @DisplayName("fetchRepository returns NotModified when server responds 304")
    fun testFetchRepositoryNotModified() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(304)
                .setHeader("ETag", "W/\"repo-etag-1\"")
        )

        val result = dataSource.fetchRepository("laralbir", "md-gh-navigator", etag = "W/\"repo-etag-1\"")

        assertTrue(result is AppResult.NotModified)
        val notModified = result as AppResult.NotModified
        assertEquals("W/\"repo-etag-1\"", notModified.etag)
    }

    @Test
    @DisplayName("fetchRepository returns Error on 404 Not Found")
    fun testFetchRepositoryNotFound() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"message":"Not Found"}""")
        )

        val result = dataSource.fetchRepository("laralbir", "unknown-repo")

        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals(404, error.code)
    }

    @Test
    @DisplayName("fetchRawContent downloads raw markdown text accurately")
    fun testFetchRawContent() = runTest {
        val markdownText = "# Guía Técnica\n\nEste es un documento Markdown de prueba."
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("ETag", "W/\"raw-etag-99\"")
                .setBody(markdownText)
        )

        val result = dataSource.fetchRawContent(server.url("/raw/file.md").toString())

        assertTrue(result is AppResult.Success)
        val success = result as AppResult.Success
        assertEquals(markdownText, success.data)
        assertEquals("W/\"raw-etag-99\"", success.etag)
    }
}
