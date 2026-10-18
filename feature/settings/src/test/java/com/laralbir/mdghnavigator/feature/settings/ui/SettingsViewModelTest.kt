package com.laralbir.mdghnavigator.feature.settings.ui

import app.cash.turbine.test
import com.laralbir.mdghnavigator.core.datastore.UserPreferences
import com.laralbir.mdghnavigator.core.datastore.UserPreferencesRepository
import com.laralbir.mdghnavigator.feature.settings.storage.StorageInfo
import com.laralbir.mdghnavigator.feature.settings.storage.StorageManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val preferencesRepository: UserPreferencesRepository = mockk(relaxed = true)
    private val storageManager: StorageManager = mockk(relaxed = true)
    private val prefsFlow = MutableStateFlow(
        UserPreferences(
            themeMode = "scholar",
            fontScale = 1.0f,
            fontFamily = "literata",
            languageCode = "system"
        )
    )

    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { preferencesRepository.userPreferences } returns prefsFlow
        coEvery { storageManager.calculateStorageUsage() } returns StorageInfo(
            cacheSizeBytes = 10485760L,
            formattedCacheSize = "10.0 MB"
        )
        viewModel = SettingsViewModel(preferencesRepository, storageManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("Initial state loads preferences and storage usage")
    fun testInitialState() = runTest {
        viewModel.uiState.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            assertEquals("scholar", state.themeMode)
            assertEquals(1.0f, state.fontScale)
            assertEquals("literata", state.fontFamily)
            assertEquals("system", state.languageCode)
            assertEquals("10.0 MB", state.cacheSizeFormatted)
        }
    }

    @Test
    @DisplayName("SetTheme delegates to repository")
    fun testSetTheme() = runTest {
        viewModel.onIntent(SettingsIntent.SetTheme("paper"))
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { preferencesRepository.setThemeMode("paper") }
    }

    @Test
    @DisplayName("SetFontScale and SetFontFamily delegate to repository")
    fun testFontSettings() = runTest {
        viewModel.onIntent(SettingsIntent.SetFontScale(1.2f))
        viewModel.onIntent(SettingsIntent.SetFontFamily("inter"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { preferencesRepository.setFontScale(1.2f) }
        coVerify(exactly = 1) { preferencesRepository.setFontFamily("inter") }
    }

    @Test
    @DisplayName("SetLanguage updates repository")
    fun testSetLanguage() = runTest {
        viewModel.onIntent(SettingsIntent.SetLanguage("es"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { preferencesRepository.setLanguage("es") }
    }

    @Test
    @DisplayName("ClearCache calls storage manager and refreshes size")
    fun testClearCache() = runTest {
        coEvery { storageManager.clearCache() } returns true
        coEvery { storageManager.calculateStorageUsage() } returnsMany listOf(
            StorageInfo(10485760L, "10.0 MB"),
            StorageInfo(0L, "0.0 MB")
        )

        viewModel.onIntent(SettingsIntent.ClearCache)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 1) { storageManager.clearCache() }
        assertFalse(viewModel.uiState.value.isClearingCache)
    }
}
