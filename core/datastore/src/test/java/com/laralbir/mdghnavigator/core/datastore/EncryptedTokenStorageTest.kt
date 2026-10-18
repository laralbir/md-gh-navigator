package com.laralbir.mdghnavigator.core.datastore

import app.cash.turbine.test
import com.laralbir.mdghnavigator.core.datastore.security.CipherManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EncryptedTokenStorageTest {

    private lateinit var cipherManager: CipherManager
    private lateinit var tokenStorage: EncryptedTokenStorage
    private val inMemoryStore = mutableMapOf<String, String>()
    private val storeFlow = MutableStateFlow<Map<String, String>>(emptyMap())

    @BeforeEach
    fun setUp() {
        cipherManager = mockk()

        // Mock cipher behavior for testing
        every { cipherManager.encrypt(any()) } answers { "enc_${firstArg<String>()}" }
        every { cipherManager.decrypt(any()) } answers { firstArg<String>().removePrefix("enc_") }

        tokenStorage = EncryptedTokenStorage(
            cipherManager = cipherManager,
            reader = { key -> inMemoryStore[key] },
            writer = { key, value ->
                if (value == null) {
                    inMemoryStore.remove(key)
                } else {
                    inMemoryStore[key] = value
                }
                storeFlow.value = inMemoryStore.toMap()
            },
            flowReader = { _ -> storeFlow }
        )
    }

    @Test
    fun `should encrypt and save token`() = runTest {
        tokenStorage.saveToken("github_token", "ghp_secret12345")

        // In underlying storage, value should be encrypted
        assertEquals("enc_ghp_secret12345", inMemoryStore["github_token"])

        // When retrieved, it should be decrypted
        val retrieved = tokenStorage.getToken("github_token")
        assertEquals("ghp_secret12345", retrieved)
    }

    @Test
    fun `should return null when token does not exist`() = runTest {
        val retrieved = tokenStorage.getToken("non_existent_token")
        assertNull(retrieved)
    }

    @Test
    fun `should delete token properly`() = runTest {
        tokenStorage.saveToken("github_token", "ghp_secret12345")
        assertEquals("ghp_secret12345", tokenStorage.getToken("github_token"))

        tokenStorage.deleteToken("github_token")
        assertNull(tokenStorage.getToken("github_token"))
        assertNull(inMemoryStore["github_token"])
    }
}
