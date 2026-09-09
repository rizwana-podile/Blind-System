package org.sightguide.core.security

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Manages cryptographically secure consent tokens for caregiver pairing
 * and generates audit trail signatures.
 */
object CaregiverTokenManager {

    private val secureRandom = SecureRandom()
    private const val CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Excludes ambiguous 0, O, 1, I for spoken clarity

    /**
     * Generates a 6-character high-entropy pairing code easy to speak or hear aloud.
     * e.g. "H7-9K2"
     */
    fun generatePairingCode(): String {
        val sb = StringBuilder(6)
        for (i in 0 until 6) {
            val index = secureRandom.nextInt(CHARACTERS.length)
            sb.append(CHARACTERS[index])
        }
        return "${sb.substring(0, 3)}-${sb.substring(3)}"
    }

    /**
     * Calculates SHA-256 integrity hash for an audit log entry.
     */
    fun calculateHash(payload: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(payload.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
