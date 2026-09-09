package org.sightguide.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaregiverTokenManagerTest {

    @Test
    fun generatePairingCode_returnsFormattedCode() {
        val code = CaregiverTokenManager.generatePairingCode()
        assertEquals(7, code.length) // 3 chars + '-' + 3 chars
        assertEquals('-', code[3])

        // Verify no confusing characters (0, O, 1, I) are generated
        assertTrue(!code.contains('0'))
        assertTrue(!code.contains('O'))
        assertTrue(!code.contains('1'))
        assertTrue(!code.contains('I'))
    }

    @Test
    fun calculateHash_returnsDeterministicSha256() {
        val text = "SOS_TRIGGERED|1725890000000|Coords(37.77,-122.41)"
        val hash1 = CaregiverTokenManager.calculateHash(text)
        val hash2 = CaregiverTokenManager.calculateHash(text)

        assertEquals(64, hash1.length) // 256 bits = 64 hex characters
        assertEquals(hash1, hash2)

        val hash3 = CaregiverTokenManager.calculateHash("different text")
        assertNotEquals(hash1, hash3)
    }
}
