package org.sightguide.core.logging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeLoggerTest {

    @Test
    fun sanitize_redactsPhoneNumbers() {
        val original = "Calling emergency contact at +1-555-123-4567 immediately"
        val sanitized = SafeLogger.sanitize(original)

        assertFalse(sanitized.contains("555-123-4567"))
        assertTrue(sanitized.contains("[REDACTED_PHONE]"))
    }

    @Test
    fun sanitize_redactsPreciseCoordinates() {
        val original = "User location: 37.774929, -122.419416 facing North"
        val sanitized = SafeLogger.sanitize(original)

        assertFalse(sanitized.contains("37.774929"))
        assertTrue(sanitized.contains("[REDACTED_COORDINATES]"))
    }

    @Test
    fun sanitize_preservesNonSensitiveText() {
        val original = "Walking guidance active. Step count: 120."
        val sanitized = SafeLogger.sanitize(original)

        assertEquals(original, sanitized)
    }
}
