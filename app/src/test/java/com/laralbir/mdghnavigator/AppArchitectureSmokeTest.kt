package com.laralbir.mdghnavigator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AppArchitectureSmokeTest {

    private fun calculateVersionCode(versionName: String): Int {
        val clean = versionName.split("-")[0].removePrefix("v")
        val parts = clean.split(".")
        val major = parts.getOrNull(0)?.toIntOrNull() ?: 1
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
        return major * 10000 + minor * 100 + patch
    }

    @Test
    @DisplayName("Version code calculation matches SemVer specification")
    fun testVersionCodeCalculation() {
        assertEquals(10000, calculateVersionCode("1.0.0"))
        assertEquals(10203, calculateVersionCode("1.2.3"))
        assertEquals(20501, calculateVersionCode("v2.5.1-alpha"))
        assertEquals(10000, calculateVersionCode("v1.0.0-rc2"))
    }
}

