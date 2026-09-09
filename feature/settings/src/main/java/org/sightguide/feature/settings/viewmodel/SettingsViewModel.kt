package org.sightguide.feature.settings.viewmodel

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
import org.sightguide.core.storage.datastore.UserSettingsDataStore

/**
 * ViewModel managing accessibility configurations, speech rate/pitch, and tactile intensity.
 */
class SettingsViewModel(
    private val userSettingsDataStore: UserSettingsDataStore,
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager,
    private val earconPlayer: EarconPlayer = EarconPlayer()
) : ViewModel() {

    data class SettingsUiState(
        val speechRate: Float = 1.0f,
        val speechPitch: Float = 1.0f,
        val isHighContrastTheme: Boolean = true,
        val hapticLevel: Int = 2,
        val isAudioChimesEnabled: Boolean = true,
        val sosCountdownSeconds: Int = 5
    )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userSettingsDataStore.speechRate.collect { rate ->
                _uiState.update { it.copy(speechRate = rate) }
                ttsManager.setSpeechRate(rate)
            }
        }
        viewModelScope.launch {
            userSettingsDataStore.speechPitch.collect { pitch ->
                _uiState.update { it.copy(speechPitch = pitch) }
                ttsManager.setPitch(pitch)
            }
        }
        viewModelScope.launch {
            userSettingsDataStore.isHighContrastTheme.collect { isHigh ->
                _uiState.update { it.copy(isHighContrastTheme = isHigh) }
            }
        }
        viewModelScope.launch {
            userSettingsDataStore.hapticLevel.collect { level ->
                _uiState.update { it.copy(hapticLevel = level) }
            }
        }
        viewModelScope.launch {
            userSettingsDataStore.isAudioChimesEnabled.collect { enabled ->
                _uiState.update { it.copy(isAudioChimesEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            userSettingsDataStore.sosCountdownSeconds.collect { sec ->
                _uiState.update { it.copy(sosCountdownSeconds = sec) }
            }
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            userSettingsDataStore.setSpeechRate(rate)
        }
    }

    fun setSpeechPitch(pitch: Float) {
        viewModelScope.launch {
            userSettingsDataStore.setSpeechPitch(pitch)
        }
    }

    fun toggleHighContrastTheme() {
        hapticManager.confirm()
        val next = !_uiState.value.isHighContrastTheme
        viewModelScope.launch {
            userSettingsDataStore.setHighContrastTheme(next)
            ttsManager.speak(if (next) "High contrast dark mode enabled" else "High contrast light mode enabled")
        }
    }

    fun setHapticLevel(level: Int) {
        viewModelScope.launch {
            userSettingsDataStore.setHapticLevel(level)
            hapticManager.confirm()
            ttsManager.speak("Tactile feedback level set to $level")
        }
    }

    fun toggleAudioChimes() {
        hapticManager.confirm()
        val next = !_uiState.value.isAudioChimesEnabled
        viewModelScope.launch {
            userSettingsDataStore.setAudioChimesEnabled(next)
            if (next) earconPlayer.playCommandRecognized()
            ttsManager.speak(if (next) "Auditory chimes enabled" else "Auditory chimes silenced")
        }
    }

    fun testVoiceSpeech() {
        hapticManager.confirm()
        ttsManager.speak("This is a sample of SIGHTGUIDE speaking at your chosen speed and pitch.")
    }

    fun testHapticFeedback() {
        hapticManager.confirm()
        ttsManager.speak("Testing tactile pulse.")
    }
}
