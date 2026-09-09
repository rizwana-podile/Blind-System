package org.sightguide.feature.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.location.LocationClient
import org.sightguide.core.storage.datastore.UserSettingsDataStore
import org.sightguide.feature.dashboard.model.DashboardUiState
import org.sightguide.feature.dashboard.model.GpsStatus

/**
 * ViewModel orchestrating the primary accessible dashboard, system status monitors,
 * and voice prompts.
 */
class DashboardViewModel(
    private val locationClient: LocationClient,
    private val userSettingsDataStore: UserSettingsDataStore,
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeVitals()
    }

    private fun observeVitals() {
        viewModelScope.launch {
            userSettingsDataStore.isLocationSharingActive.collect { isSharing ->
                _uiState.update { it.copy(isLocationSharingActive = isSharing) }
            }
        }

        val gpsEnabled = locationClient.isLocationServiceEnabled()
        _uiState.update {
            it.copy(
                gpsStatus = if (gpsEnabled) GpsStatus.ACTIVE else GpsStatus.DISABLED
            )
        }
    }

    fun onDashboardOpened() {
        hapticManager.confirm()
        ttsManager.speak("SIGHTGUIDE. What can I help with?")
    }

    fun onVoiceTriggerClicked() {
        hapticManager.confirm()
        _uiState.update { it.copy(isVoiceListening = true) }
        ttsManager.speak("Listening. Say your command.")
    }

    fun onFeatureTileClicked(title: String, destinationSummary: String) {
        hapticManager.confirm()
        ttsManager.speak("Opening $title. $destinationSummary")
    }

    fun onStatusBadgeClicked(announcement: String) {
        hapticManager.confirm()
        ttsManager.speak(announcement)
    }
}
