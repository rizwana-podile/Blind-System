package org.sightguide.core.accessibility.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * High-precision tactile communication manager.
 * Emits distinct, human-recognizable haptic waveforms for pedestrian guidance,
 * obstacle proximity, and emergency alerts.
 */
class HapticPatternManager(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Crisp double pulse confirming a successful selection or completed action.
     * Pattern: 40ms pulse, 60ms gap, 40ms pulse.
     */
    fun confirm() {
        playPattern(
            timings = longArrayOf(0, 40, 60, 40),
            amplitudes = intArrayOf(0, 180, 0, 255)
        )
    }

    /**
     * Asymmetrical rising pulse indicating a turn to the LEFT.
     */
    fun turnLeft() {
        playPattern(
            timings = longArrayOf(0, 80, 80, 140),
            amplitudes = intArrayOf(0, 120, 0, 255)
        )
    }

    /**
     * Asymmetrical falling pulse indicating a turn to the RIGHT.
     */
    fun turnRight() {
        playPattern(
            timings = longArrayOf(0, 140, 80, 80),
            amplitudes = intArrayOf(0, 255, 0, 120)
        )
    }

    /**
     * Urgent triple pulse alerting the user to an imminent obstacle or drop-off.
     * Pattern: 3 rapid aggressive buzzes.
     */
    fun hazardAlert() {
        playPattern(
            timings = longArrayOf(0, 90, 40, 90, 40, 90),
            amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
        )
    }

    /**
     * Deep, rhythmic 1-second pulse for the SOS emergency countdown.
     */
    fun sosCountdownBeat() {
        playPattern(
            timings = longArrayOf(0, 200, 100, 200),
            amplitudes = intArrayOf(0, 255, 0, 255)
        )
    }

    /**
     * Soft single tap signaling end of OCR document reading.
     */
    fun textReadDone() {
        playPattern(
            timings = longArrayOf(0, 50),
            amplitudes = intArrayOf(0, 100)
        )
    }

    /**
     * Cancels any active ongoing vibration pattern.
     */
    fun cancel() {
        try {
            vibrator?.cancel()
        } catch (_: Exception) {
            // Gracefully ignore if vibrator is unavailable
        }
    }

    private fun playPattern(timings: LongArray, amplitudes: IntArray) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vib.hasAmplitudeControl()) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vib.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, -1)
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(timings, -1)
            }
        } catch (_: Exception) {
            // Graceful fallback: never crash if vibration service fails
        }
    }
}
