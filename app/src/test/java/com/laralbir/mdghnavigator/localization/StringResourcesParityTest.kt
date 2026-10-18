package com.laralbir.mdghnavigator.localization

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.util.regex.Pattern

class StringResourcesParityTest {

    private val moduleResDirs = listOf(
        "core/designsystem/src/main/res",
        "feature/library/src/main/res",
        "feature/reader/src/main/res",
        "feature/navigator/src/main/res",
        "feature/tts/src/main/res",
        "feature/annotations/src/main/res",
        "app/src/main/res"
    )

    private fun extractStringKeys(xmlFile: File): Set<String> {
        if (!xmlFile.exists()) return emptySet()
        val content = xmlFile.readText()
        val pattern = Pattern.compile("""<string\s+name="([^"]+)"""")
        val matcher = pattern.matcher(content)
        val keys = mutableSetOf<String>()
        while (matcher.find()) {
            val key = matcher.group(1)
            if (!key.isNullOrBlank()) {
                keys.add(key)
            }
        }
        return keys
    }

    private fun findRootDir(): File {
        var current: File = File(System.getProperty("user.dir") ?: ".")
        while (current.parentFile != null && !File(current, "settings.gradle.kts").exists()) {
            val parent = current.parentFile ?: break
            current = parent
        }
        return current
    }

    @Test
    @DisplayName("Every module with strings must have identical keys in values and values-es")
    fun testStringParityAcrossLocales() {
        val rootDir = findRootDir()

        for (relPath in moduleResDirs) {
            val resDir = File(rootDir, relPath)
            val defaultStrings = File(resDir, "values/strings.xml")
            val spanishStrings = File(resDir, "values-es/strings.xml")

            assertTrue(
                defaultStrings.exists(),
                "Default strings.xml must exist in $relPath/values/strings.xml"
            )
            assertTrue(
                spanishStrings.exists(),
                "Spanish strings.xml must exist in $relPath/values-es/strings.xml"
            )

            val defaultKeys = extractStringKeys(defaultStrings)
            val spanishKeys = extractStringKeys(spanishStrings)

            assertTrue(
                defaultKeys.isNotEmpty(),
                "Expected non-empty string keys in $relPath/values/strings.xml"
            )

            val missingInSpanish = defaultKeys - spanishKeys
            val missingInDefault = spanishKeys - defaultKeys

            assertEquals(
                emptySet<String>(),
                missingInSpanish,
                "Keys defined in $relPath/values/strings.xml but missing in values-es: $missingInSpanish"
            )

            assertEquals(
                emptySet<String>(),
                missingInDefault,
                "Keys defined in $relPath/values-es/strings.xml but missing in values: $missingInDefault"
            )
        }
    }
}

