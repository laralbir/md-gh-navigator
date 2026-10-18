package com.laralbir.mdghnavigator.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laralbir.mdghnavigator.core.datastore.UserPreferencesRepository
import com.laralbir.mdghnavigator.core.designsystem.localization.AppLocaleManager
import com.laralbir.mdghnavigator.feature.settings.storage.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
        refreshStorage()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferences.collect { prefs ->
                _uiState.update { current ->
                    current.copy(
                        themeMode = prefs.themeMode,
                        fontScale = prefs.fontScale,
                        fontFamily = prefs.fontFamily,
                        languageCode = prefs.languageCode
                    )
                }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetTheme -> setTheme(intent.theme)
            is SettingsIntent.SetFontScale -> setFontScale(intent.scale)
            is SettingsIntent.SetFontFamily -> setFontFamily(intent.family)
            is SettingsIntent.SetLanguage -> setLanguage(intent.code)
            is SettingsIntent.ClearCache -> clearCache()
            is SettingsIntent.RefreshStorage -> refreshStorage()
            is SettingsIntent.ClearMessage -> _uiState.update { it.copy(message = null) }
        }
    }

    private fun setTheme(theme: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(theme)
        }
    }

    private fun setFontScale(scale: Float) {
        viewModelScope.launch {
            preferencesRepository.setFontScale(scale)
        }
    }

    private fun setFontFamily(family: String) {
        viewModelScope.launch {
            preferencesRepository.setFontFamily(family)
        }
    }

    private fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(languageCode)
            AppLocaleManager.setLocale(languageCode)
        }
    }

    private fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearingCache = true) }
            val cleared = storageManager.clearCache()
            val storageInfo = storageManager.calculateStorageUsage()
            _uiState.update {
                it.copy(
                    isClearingCache = false,
                    cacheSizeFormatted = storageInfo.formattedCacheSize,
                    message = if (cleared) "Caché liberada correctamente" else null
                )
            }
        }
    }

    private fun refreshStorage() {
        viewModelScope.launch {
            val storageInfo = storageManager.calculateStorageUsage()
            _uiState.update {
                it.copy(cacheSizeFormatted = storageInfo.formattedCacheSize)
            }
        }
    }
}
