package com.laralbir.mdghnavigator.core.network.datasource

import com.laralbir.mdghnavigator.core.common.result.AppResult
import com.laralbir.mdghnavigator.core.network.api.GitHubApiService
import com.laralbir.mdghnavigator.core.network.model.GitHubContentDto
import com.laralbir.mdghnavigator.core.network.model.GitHubReleaseDto
import com.laralbir.mdghnavigator.core.network.model.GitHubRepoDto
import com.laralbir.mdghnavigator.core.network.model.GitHubTreeDto
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRemoteDataSource @Inject constructor(
    private val apiService: GitHubApiService
) {

    suspend fun fetchRepository(
        owner: String,
        repo: String,
        etag: String? = null
    ): AppResult<GitHubRepoDto> {
        return safeApiCall { apiService.getRepository(owner, repo, etag) }
    }

    suspend fun fetchTree(
        owner: String,
        repo: String,
        treeSha: String,
        etag: String? = null
    ): AppResult<GitHubTreeDto> {
        return safeApiCall { apiService.getGitTree(owner, repo, treeSha, recursive = 1, etag = etag) }
    }

    suspend fun fetchContent(
        owner: String,
        repo: String,
        path: String,
        ref: String? = null,
        etag: String? = null
    ): AppResult<GitHubContentDto> {
        return safeApiCall { apiService.getContent(owner, repo, path, ref, etag) }
    }

    suspend fun fetchRawContent(
        rawUrl: String,
        etag: String? = null
    ): AppResult<String> {
        return try {
            val response = apiService.getRawContent(rawUrl, etag)
            val responseEtag = response.headers()["ETag"]
            when {
                response.code() == 304 -> AppResult.NotModified(responseEtag ?: etag)
                response.isSuccessful -> {
                    val text = response.body()?.string() ?: ""
                    AppResult.Success(text, responseEtag)
                }
                else -> AppResult.Error(response.message(), code = response.code())
            }
        } catch (e: Exception) {
            AppResult.Error(e.localizedMessage ?: "Error de red al descargar contenido", throwable = e)
        }
    }

    suspend fun fetchLatestRelease(
        owner: String,
        repo: String
    ): AppResult<GitHubReleaseDto> {
        return safeApiCall { apiService.getLatestRelease(owner, repo) }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): AppResult<T> {
        return try {
            val response = call()
            val etag = response.headers()["ETag"]
            when {
                response.code() == 304 -> AppResult.NotModified(etag)
                response.isSuccessful && response.body() != null -> AppResult.Success(response.body()!!, etag)
                else -> AppResult.Error(response.message(), code = response.code())
            }
        } catch (e: Exception) {
            AppResult.Error(e.localizedMessage ?: "Error en la comunicación con GitHub", throwable = e)
        }
    }
}

