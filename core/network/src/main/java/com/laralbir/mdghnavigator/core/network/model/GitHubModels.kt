package com.laralbir.mdghnavigator.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepoDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val private: Boolean = false,
    val description: String? = null,
    @SerialName("default_branch") val defaultBranch: String = "main",
    @SerialName("html_url") val htmlUrl: String = "",
    val owner: GitHubOwnerDto? = null
)

@Serializable
data class GitHubOwnerDto(
    val id: Long,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)

@Serializable
data class GitHubTreeDto(
    val sha: String,
    val url: String,
    val tree: List<GitHubTreeNodeDto>,
    val truncated: Boolean = false
)

@Serializable
data class GitHubTreeNodeDto(
    val path: String,
    val mode: String,
    val type: String, // "blob" or "tree"
    val sha: String,
    val size: Long? = null,
    val url: String
)

@Serializable
data class GitHubContentDto(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long,
    val url: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("download_url") val downloadUrl: String? = null,
    val type: String,
    val content: String? = null,
    val encoding: String? = null
)

@Serializable
data class GitHubReleaseDto(
    val id: Long,
    @SerialName("tag_name") val tagName: String,
    val name: String? = null,
    val body: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    val prerelease: Boolean = false
)
