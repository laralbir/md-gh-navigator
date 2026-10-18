package com.laralbir.mdghnavigator.core.datastore

import com.laralbir.mdghnavigator.core.datastore.security.CipherManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TokenStorage {
    suspend fun saveToken(key: String, token: String)
    suspend fun getToken(key: String): String?
    suspend fun deleteToken(key: String)
    fun observeToken(key: String): Flow<String?>
}

class EncryptedTokenStorage(
    private val cipherManager: CipherManager,
    private val reader: suspend (key: String) -> String?,
    private val writer: suspend (key: String, encryptedValue: String?) -> Unit,
    private val flowReader: (key: String) -> Flow<Map<String, String>>
) : TokenStorage {

    override suspend fun saveToken(key: String, token: String) {
        val encrypted = cipherManager.encrypt(token)
        writer(key, encrypted)
    }

    override suspend fun getToken(key: String): String? {
        val encrypted = reader(key) ?: return null
        return try {
            cipherManager.decrypt(encrypted)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteToken(key: String) {
        writer(key, null)
    }

    override fun observeToken(key: String): Flow<String?> {
        return flowReader(key).map { map ->
            val encrypted = map[key] ?: return@map null
            try {
                cipherManager.decrypt(encrypted)
            } catch (e: Exception) {
                null
            }
        }
    }
}
