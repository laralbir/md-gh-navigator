package com.laralbir.mdghnavigator.core.designsystem.localization

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Representation of a supported application language.
 *
 * @property code BCP-47 language tag or "system" for system default
 * @property englishName Name of the language in English
 * @property nativeName Native name of the language
 */
data class AppLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String
)

/**
 * Manager for inspecting and updating per-app language settings across all supported Android versions.
 * Integrates with Android 13+ per-app language preferences and AppCompat fallback.
 */
object AppLocaleManager {

    val SYSTEM_DEFAULT = AppLanguage(
        code = "system",
        englishName = "System default",
        nativeName = "Por defecto del sistema"
    )

    val ENGLISH = AppLanguage(
        code = "en",
        englishName = "English",
        nativeName = "English"
    )

    val SPANISH = AppLanguage(
        code = "es",
        englishName = "Spanish",
        nativeName = "Español"
    )

    /**
     * List of all supported languages for the application.
     */
    val supportedLanguages: List<AppLanguage> = listOf(
        SYSTEM_DEFAULT,
        SPANISH,
        ENGLISH
    )

    /**
     * Sets the application locale. If [languageCode] is "system" or blank, restores system default.
     */
    fun setLocale(languageCode: String) {
        val localeList = if (languageCode.isBlank() || languageCode.equals("system", ignoreCase = true)) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageCode)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    /**
     * Returns the currently active language code ("system", "es", "en", etc.).
     */
    fun getCurrentLanguageCode(): String {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        return if (currentLocales.isEmpty) {
            "system"
        } else {
            currentLocales[0]?.language ?: "system"
        }
    }

    /**
     * Resolves the [AppLanguage] corresponding to a language code, falling back to [SYSTEM_DEFAULT].
     */
    fun resolveLanguage(code: String): AppLanguage {
        return supportedLanguages.firstOrNull {
            it.code.equals(code, ignoreCase = true)
        } ?: SYSTEM_DEFAULT
    }
}

