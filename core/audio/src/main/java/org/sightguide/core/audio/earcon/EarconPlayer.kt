package org.sightguide.core.audio.earcon

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * High-reliability earcon (auditory chime) player.
 * Uses a hardware-accelerated ToneGenerator to provide instantaneous, zero-latency
 * auditory feedback on every Android device without network or external media file dependencies.
 */
class EarconPlayer {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
        } catch (e: Exception) {
            Log.w("EarconPlayer", "Failed to initialize ToneGenerator", e)
        }
    }

    /**
     * Plays gentle ascending chime when voice recognition activates.
     */
    fun playListeningStart() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
    }

    /**
     * Plays crisp pleasant confirmation tone when a voice command is understood.
     */
    fun playCommandRecognized() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
    }

    /**
     * Plays triumph chime when user arrives at their destination or waypoint.
     */
    fun playWaypointReached() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
    }

    /**
     * Plays resonant warning tone when an obstacle or hazard is detected.
     */
    fun playHazardAlert() {
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE, 300)
    }

    /**
     * Plays soft affirmation chime when text/OCR is recognized and ready to read.
     */
    fun playOcrSuccess() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 120)
    }

    /**
     * Releases audio hardware resources.
     */
    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {
        }
    }
}
