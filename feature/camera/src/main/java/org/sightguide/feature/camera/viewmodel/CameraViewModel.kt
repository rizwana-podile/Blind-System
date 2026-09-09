package org.sightguide.feature.camera.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.earcon.EarconPlayer
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.feature.camera.model.DetectedObstacle
import java.util.Locale

/**
 * Camera ViewModel managing real-time obstacle identification,
 * item searching, and auditory scene descriptions.
 */
class CameraViewModel(
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager,
    private val earconPlayer: EarconPlayer = EarconPlayer()
) : ViewModel() {

    data class CameraUiState(
        val detectedObstacles: List<DetectedObstacle> = emptyList(),
        val searchTarget: String? = null,
        val isSearching: Boolean = false,
        val isTorchEnabled: Boolean = false,
        val lastSummary: String = "Camera active. Tap Describe Scene to inspect surroundings."
    )

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var lastHazardAlertTimeMillis = 0L

    fun onObstaclesDetected(obstacles: List<DetectedObstacle>) {
        _uiState.update { it.copy(detectedObstacles = obstacles) }

        // 1. Hazard proximity check (e.g. close obstacle in walking path)
        val imminentHazard = obstacles.firstOrNull { it.isHazard }
        val now = System.currentTimeMillis()
        if (imminentHazard != null && (now - lastHazardAlertTimeMillis > 3000L)) {
            lastHazardAlertTimeMillis = now
            hapticManager.hazardAlert()
            earconPlayer.playHazardAlert()
            ttsManager.speak("Warning: ${imminentHazard.label} close ahead", interruptIfSpeaking = true)
        }

        // 2. Object search check
        val target = _uiState.value.searchTarget
        if (!target.isNullOrBlank() && _uiState.value.isSearching) {
            val matched = obstacles.firstOrNull {
                it.label.lowercase(Locale.ROOT).contains(target.lowercase(Locale.ROOT))
            }
            if (matched != null) {
                hapticManager.confirm()
                earconPlayer.playCommandRecognized()
                val phrase = "Found $target! ${matched.spokenDescription}"
                ttsManager.speak(phrase)
                _uiState.update { it.copy(isSearching = false) }
            }
        }
    }

    fun describeScene() {
        hapticManager.confirm()
        val obstacles = _uiState.value.detectedObstacles
        val summary = if (obstacles.isEmpty()) {
            "No distinct obstacles or people detected in the current view. Path appears clear."
        } else {
            val descriptions = obstacles.take(3).joinToString("; ") { it.spokenDescription }
            "In front of you: $descriptions."
        }

        _uiState.update { it.copy(lastSummary = summary) }
        ttsManager.speak(summary)
    }

    fun startSearch(targetObject: String) {
        hapticManager.confirm()
        _uiState.update {
            it.copy(
                searchTarget = targetObject,
                isSearching = true
            )
        }
        ttsManager.speak("Searching for $targetObject. Pan your camera slowly.")
    }

    fun stopSearch() {
        hapticManager.confirm()
        _uiState.update { it.copy(searchTarget = null, isSearching = false) }
        ttsManager.speak("Object search stopped")
    }

    fun toggleTorch() {
        hapticManager.confirm()
        val next = !_uiState.value.isTorchEnabled
        _uiState.update { it.copy(isTorchEnabled = next) }
        ttsManager.speak(if (next) "Flashlight turned on" else "Flashlight turned off")
    }
}
