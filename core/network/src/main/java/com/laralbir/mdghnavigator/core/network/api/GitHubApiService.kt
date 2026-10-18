package com.laralbir.mdghnavigator.core.network.api

import com.laralbir.mdghnavigator.core.network.model.GitHubContentDto
import com.laralbir.mdghnavigator.core.network.model.GitHubReleaseDto
import com.laralbir.mdghnavigator.core.network.model.GitHubRepoDto
import com.laralbir.mdghnavigator.core.network.model.GitHubTreeDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GitHubApiService {

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("If-None-Match") etag: String? = null
    ): Response<GitHubRepoDto>

    @GET("repos/{owner}/{repo}/git/trees/{tree_sha}")
    suspend fun getGitTree(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("tree_sha") treeSha: String,
        @Query("recursive") recursive: Int = 1,
        @Header("If-None-Match") etag: String? = null
    ): Response<GitHubTreeDto>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Query("ref") ref: String? = null,
        @Header("If-None-Match") etag: String? = null
    ): Response<GitHubContentDto>

    @GET
    @Headers("Accept: application/vnd.github.raw, application/vnd.github.v3.raw, application/json;q=0.8, */*;q=0.5")
    suspend fun getRawContent(
        @Url rawUrl: String,
        @Header("If-None-Match") etag: String? = null
    ): Response<ResponseBody>

    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<GitHubReleaseDto>
}
