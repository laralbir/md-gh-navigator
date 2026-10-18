package com.laralbir.mdghnavigator.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Entidad inmutable que representa las preferencias de personalización del usuario.
 *
 * @property themeMode Tema de visualización: "scholar" (NXTPAPER e-ink), "paper" (minimalista), "neon" (alto contraste)
 * @property fontScale Escala tipográfica: 0.8x a 1.6x
 * @property fontFamily Familia tipográfica: "literata" (Serif), "inter" (Sans-Serif), "system" (Sistema)
 * @property languageCode Código de idioma BCP-47 o "system" para el idioma del dispositivo
 */
data class UserPreferences(
    val themeMode: String = "paper",
    val fontScale: Float = 1.0f,
    val fontFamily: String = "literata",
    val languageCode: String = "system"
)

/**
 * Contrato de repositorio reactivo para la gestión y persistencia de preferencias de usuario.
 */
interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setThemeMode(theme: String)
    suspend fun setFontScale(scale: Float)
    suspend fun setFontFamily(family: String)
    suspend fun setLanguage(languageCode: String)
}

/**
 * Implementación de [UserPreferencesRepository] respaldada por Jetpack DataStore Preferences.
 */
@Singleton
class DataStoreUserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[PreferencesKeys.THEME_MODE] ?: "paper",
            fontScale = prefs[PreferencesKeys.FONT_SCALE] ?: 1.0f,
            fontFamily = prefs[PreferencesKeys.FONT_FAMILY] ?: "literata",
            languageCode = prefs[PreferencesKeys.LANGUAGE_CODE] ?: "system"
        )
    }

    override suspend fun setThemeMode(theme: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = theme
        }
    }

    override suspend fun setFontScale(scale: Float) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.FONT_SCALE] = scale
        }
    }

    override suspend fun setFontFamily(family: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.FONT_FAMILY] = family
        }
    }

    override suspend fun setLanguage(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.LANGUAGE_CODE] = languageCode
        }
    }
}
