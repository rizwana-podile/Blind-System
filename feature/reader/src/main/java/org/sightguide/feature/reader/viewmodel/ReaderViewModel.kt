package org.sightguide.feature.reader.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.earcon.EarconPlayer
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.feature.reader.model.ReadingDocument

/**
 * ViewModel managing document OCR scanning, classification, and text-to-speech reading playback.
 */
class ReaderViewModel(
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager,
    private val earconPlayer: EarconPlayer = EarconPlayer()
) : ViewModel() {

    data class ReaderUiState(
        val document: ReadingDocument? = null,
        val isReading: Boolean = false,
        val isPaused: Boolean = false,
        val statusMessage: String = "Position camera over text to read."
    )

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var latestCandidateDocument: ReadingDocument? = null

    fun onDocumentScanned(doc: ReadingDocument?) {
        latestCandidateDocument = doc
        if (doc != null && !_uiState.value.isReading) {
            _uiState.update {
                it.copy(
                    document = doc,
                    statusMessage = "${doc.documentType.spokenName} in view. Double tap Read Now to listen."
                )
            }
        }
    }

    fun readNow() {
        val doc = latestCandidateDocument ?: _uiState.value.document
        if (doc == null || doc.rawText.isBlank()) {
            hapticManager.confirm()
            ttsManager.speak("No readable text detected. Please hold your camera steady, check lighting, or move closer to the page.")
            return
        }

        hapticManager.confirm()
        earconPlayer.playOcrSuccess()

        val prefix = buildString {
            append("${doc.documentType.spokenName} detected. ")
            if (doc.keyHighlight != null) {
                append("Key information: ${doc.keyHighlight}. ")
            }
            append("Reading content: ")
        }

        _uiState.update {
            it.copy(
                document = doc,
                isReading = true,
                isPaused = false,
                statusMessage = "Reading ${doc.documentType.spokenName}..."
            )
        }

        ttsManager.speak(prefix + doc.rawText)
    }

    fun pauseReading() {
        hapticManager.confirm()
        ttsManager.stop()
        _uiState.update {
            it.copy(
                isReading = false,
                isPaused = true,
                statusMessage = "Reading paused."
            )
        }
        ttsManager.speak("Reading paused.")
    }

    fun resumeReading() {
        hapticManager.confirm()
        val doc = _uiState.value.document
        if (doc != null) {
            _uiState.update {
                it.copy(
                    isReading = true,
                    isPaused = false,
                    statusMessage = "Resuming reading..."
                )
            }
            ttsManager.speak("Resuming: " + doc.rawText)
        }
    }

    fun repeat() {
        hapticManager.confirm()
        ttsManager.repeatLast()
    }

    fun stopReading() {
        hapticManager.confirm()
        ttsManager.stop()
        _uiState.update {
            it.copy(
                isReading = false,
                isPaused = false,
                statusMessage = "Reading stopped."
            )
        }
        ttsManager.speak("Reading stopped.")
    }
}
