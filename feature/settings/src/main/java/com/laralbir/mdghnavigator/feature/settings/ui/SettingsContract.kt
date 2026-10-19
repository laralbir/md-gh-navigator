package com.laralbir.mdghnavigator.feature.settings.ui

data class SettingsUiState(
    val themeMode: String = "paper",
    val fontScale: Float = 1.0f,
    val fontFamily: String = "inter",
    val languageCode: String = "system",
    val cacheSizeFormatted: String = "0.0 MB",
    val isClearingCache: Boolean = false,
    val message: String? = null
)

sealed interface SettingsIntent {
    data class SetTheme(val theme: String) : SettingsIntent
    data class SetFontScale(val scale: Float) : SettingsIntent
    data class SetFontFamily(val family: String) : SettingsIntent
    data class SetLanguage(val code: String) : SettingsIntent
    data object ClearCache : SettingsIntent
    data object RefreshStorage : SettingsIntent
    data object ClearMessage : SettingsIntent
}
