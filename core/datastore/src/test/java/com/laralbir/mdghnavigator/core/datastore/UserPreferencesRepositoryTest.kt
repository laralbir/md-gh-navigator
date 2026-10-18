package com.laralbir.mdghnavigator.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserPreferencesRepositoryTest {

    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)
    private val prefsFlow = MutableStateFlow<Preferences>(emptyPreferences())
    private lateinit var repository: DataStoreUserPreferencesRepository

    @BeforeEach
    fun setUp() {
        every { dataStore.data } returns prefsFlow
        repository = DataStoreUserPreferencesRepository(dataStore)
    }

    @Test
    @DisplayName("Default preferences return paper theme, 1.0 scale, literata font and system language")
    fun testDefaultPreferences() = runTest {
        repository.userPreferences.test {
            val prefs = awaitItem()
            assertEquals("paper", prefs.themeMode)
            assertEquals(1.0f, prefs.fontScale)
            assertEquals("literata", prefs.fontFamily)
            assertEquals("system", prefs.languageCode)
        }
    }

    @Test
    @DisplayName("Preferences emit updated values from DataStore")
    fun testUpdatedPreferences() = runTest {
        val themeKey = stringPreferencesKey("theme_mode")
        val scaleKey = floatPreferencesKey("font_scale")
        val familyKey = stringPreferencesKey("font_family")
        val langKey = stringPreferencesKey("language_code")

        val customPrefs = mutablePreferencesOf(
            themeKey to "neon",
            scaleKey to 1.4f,
            familyKey to "inter",
            langKey to "es"
        )
        prefsFlow.value = customPrefs

        repository.userPreferences.test {
            val prefs = awaitItem()
            assertEquals("neon", prefs.themeMode)
            assertEquals(1.4f, prefs.fontScale)
            assertEquals("inter", prefs.fontFamily)
            assertEquals("es", prefs.languageCode)
        }
    }

    @Test
    @DisplayName("Setters invoke DataStore edit")
    fun testSetters() = runTest {
        coEvery { dataStore.updateData(any()) } returns emptyPreferences()

        repository.setThemeMode("paper")
        repository.setFontScale(1.2f)
        repository.setFontFamily("inter")
        repository.setLanguage("en")

        coVerify(exactly = 4) { dataStore.updateData(any()) }
    }
}
