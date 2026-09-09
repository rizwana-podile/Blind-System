package org.sightguide.core.common.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    @Test
    fun successResult_returnsDataAndStatus() {
        val result: AppResult<String> = AppResult.Success("Guidance Active")

        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertEquals("Guidance Active", result.getOrNull())
        assertEquals("Guidance Active", result.getOrDefault("Default"))
    }

    @Test
    fun errorResult_returnsErrorMessageAndCode() {
        val result: AppResult<String> = AppResult.Error(
            userFriendlyMessage = "GPS signal lost",
            code = ErrorCode.HARDWARE_UNAVAILABLE
        )

        assertTrue(result.isError)
        assertFalse(result.isSuccess)
        assertNull(result.getOrNull())
        assertEquals("Fallback", result.getOrDefault("Fallback"))

        val error = result as AppResult.Error
        assertEquals("GPS signal lost", error.userFriendlyMessage)
        assertEquals(ErrorCode.HARDWARE_UNAVAILABLE, error.code)
    }

    @Test
    fun map_transformsSuccessValue() {
        val result: AppResult<Int> = AppResult.Success(100)
        val mapped = result.map { it * 2 }

        assertEquals(200, mapped.getOrNull())
    }

    @Test
    fun map_preservesErrorState() {
        val result: AppResult<Int> = AppResult.Error(userFriendlyMessage = "Failed")
        val mapped = result.map { it * 2 }

        assertTrue(mapped.isError)
    }
}
