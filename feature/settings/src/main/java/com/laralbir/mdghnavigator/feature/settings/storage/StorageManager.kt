package com.laralbir.mdghnavigator.feature.settings.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

data class StorageInfo(
    val cacheSizeBytes: Long,
    val formattedCacheSize: String
)

interface StorageManager {
    suspend fun calculateStorageUsage(): StorageInfo
    suspend fun clearCache(): Boolean
}

@Singleton
class AndroidStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) : StorageManager {

    override suspend fun calculateStorageUsage(): StorageInfo = withContext(Dispatchers.IO) {
        val cacheDir = context.cacheDir
        val codeCacheDir = context.codeCacheDir
        val externalCache = context.externalCacheDir

        val totalBytes = calculateDirectorySize(cacheDir) +
                calculateDirectorySize(codeCacheDir) +
                calculateDirectorySize(externalCache)

        StorageInfo(
            cacheSizeBytes = totalBytes,
            formattedCacheSize = formatBytes(totalBytes)
        )
    }

    override suspend fun clearCache(): Boolean = withContext(Dispatchers.IO) {
        var success = true
        context.cacheDir?.let { success = deleteContents(it) && success }
        context.externalCacheDir?.let { success = deleteContents(it) && success }
        success
    }

    private fun calculateDirectorySize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateDirectorySize(file)
            } else {
                file.length()
            }
        }
        return size
    }

    private fun deleteContents(dir: File): Boolean {
        var allDeleted = true
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                allDeleted = deleteContents(file) && file.delete() && allDeleted
            } else {
                allDeleted = file.delete() && allDeleted
            }
        }
        return allDeleted
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0.0 MB"
        val df = DecimalFormat("#.##")
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> "${df.format(gb)} GB"
            mb >= 1.0 -> "${df.format(mb)} MB"
            kb >= 1.0 -> "${df.format(kb)} KB"
            else -> "$bytes B"
        }
    }
}
