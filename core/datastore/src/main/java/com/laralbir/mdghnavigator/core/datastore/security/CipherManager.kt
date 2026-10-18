package com.laralbir.mdghnavigator.core.datastore.security

interface CipherManager {
    fun encrypt(plaintext: String): String
    fun decrypt(ciphertext: String): String
}
