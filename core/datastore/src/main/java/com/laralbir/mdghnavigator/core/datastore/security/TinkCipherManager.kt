package com.laralbir.mdghnavigator.core.datastore.security

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

class TinkCipherManager(
    context: Context,
    masterKeyUri: String = "android-keystore://mdghnavigator_master_key",
    keysetPrefName: String = "mdghnavigator_tink_keyset",
    prefFileName: String = "mdghnavigator_tink_prefs"
) : CipherManager {

    private val aead: Aead

    init {
        AeadConfig.register()

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, keysetPrefName, prefFileName)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(masterKeyUri)
            .build()
            .keysetHandle

        aead = keysetHandle.getPrimitive(Aead::class.java)
    }

    override fun encrypt(plaintext: String): String {
        val encryptedBytes = aead.encrypt(plaintext.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    override fun decrypt(ciphertext: String): String {
        val decodedBytes = Base64.decode(ciphertext, Base64.NO_WRAP)
        val decryptedBytes = aead.decrypt(decodedBytes, null)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
