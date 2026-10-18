package com.laralbir.mdghnavigator.core.designsystem.localization

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AppLocaleManagerTest {

    @Test
    @DisplayName("supportedLanguages should contain system, Spanish, and English")
    fun testSupportedLanguages() {
        val languages = AppLocaleManager.supportedLanguages
        assertEquals(3, languages.size)

        val codes = languages.map { it.code }
        assertTrue(codes.contains("system"))
        assertTrue(codes.contains("es"))
        assertTrue(codes.contains("en"))
    }

    @Test
    @DisplayName("resolveLanguage should return matching language or system default fallback")
    fun testResolveLanguage() {
        assertEquals("Español", AppLocaleManager.resolveLanguage("es").nativeName)
        assertEquals("English", AppLocaleManager.resolveLanguage("en").nativeName)
        assertEquals("system", AppLocaleManager.resolveLanguage("system").code)
        assertEquals("system", AppLocaleManager.resolveLanguage("fr").code)
        assertEquals("system", AppLocaleManager.resolveLanguage("").code)
    }

    @Test
    @DisplayName("AppLanguage entries should have valid non-blank metadata")
    fun testAppLanguageMetadata() {
        for (lang in AppLocaleManager.supportedLanguages) {
            assertTrue(lang.code.isNotBlank())
            assertTrue(lang.englishName.isNotBlank())
            assertTrue(lang.nativeName.isNotBlank())
        }
    }
}

