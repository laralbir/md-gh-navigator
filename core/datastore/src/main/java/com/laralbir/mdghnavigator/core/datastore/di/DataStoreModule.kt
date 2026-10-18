package com.laralbir.mdghnavigator.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.laralbir.mdghnavigator.core.datastore.EncryptedTokenStorage
import com.laralbir.mdghnavigator.core.datastore.TokenStorage
import com.laralbir.mdghnavigator.core.datastore.security.CipherManager
import com.laralbir.mdghnavigator.core.datastore.security.TinkCipherManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")
private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideCipherManager(
        @ApplicationContext context: Context
    ): CipherManager {
        return TinkCipherManager(context)
    }

    @Provides
    @Singleton
    fun provideTokenStorage(
        @ApplicationContext context: Context,
        cipherManager: CipherManager
    ): TokenStorage {
        val dataStore = context.tokenDataStore
        return EncryptedTokenStorage(
            cipherManager = cipherManager,
            reader = { key ->
                val prefsKey = stringPreferencesKey(key)
                dataStore.data.map { it[prefsKey] }.first()
            },
            writer = { key, encryptedValue ->
                val prefsKey = stringPreferencesKey(key)
                dataStore.edit { prefs ->
                    if (encryptedValue == null) {
                        prefs.remove(prefsKey)
                    } else {
                        prefs[prefsKey] = encryptedValue
                    }
                }
            },
            flowReader = { key ->
                val prefsKey = stringPreferencesKey(key)
                dataStore.data.map { prefs ->
                    val value = prefs[prefsKey]
                    if (value != null) mapOf(key to value) else emptyMap()
                }
            }
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): com.laralbir.mdghnavigator.core.datastore.UserPreferencesRepository {
        return com.laralbir.mdghnavigator.core.datastore.DataStoreUserPreferencesRepository(
            dataStore = context.userSettingsDataStore
        )
    }
}
