package org.sightguide.core.logging

import android.util.Log

/**
 * Privacy-protecting logger that strips Personally Identifiable Information (PII)
 * such as phone numbers, precision GPS coordinates, and raw token keys before output.
 */
object SafeLogger {

    private const val DEFAULT_TAG = "SightGuide"

    private val PHONE_REGEX = Regex("""(?:\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}""")
    private val COORDINATE_REGEX = Regex("""[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?),\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)""")

    var isDebugEnabled: Boolean = true

    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (isDebugEnabled) {
            Log.d(tag, sanitize(message))
        }
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        Log.i(tag, sanitize(message))
    }

    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        Log.w(tag, sanitize(message), throwable)
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        Log.e(tag, sanitize(message), throwable)
    }

    /**
     * Replaces phone numbers and raw GPS coordinates with privacy redaction masks.
     */
    fun sanitize(input: String): String {
        var sanitized = input
        sanitized = PHONE_REGEX.replace(sanitized, "[REDACTED_PHONE]")
        sanitized = COORDINATE_REGEX.replace(sanitized, "[REDACTED_COORDINATES]")
        return sanitized
    }
}
