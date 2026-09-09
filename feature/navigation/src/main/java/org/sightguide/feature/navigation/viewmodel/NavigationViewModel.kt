package org.sightguide.feature.navigation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.earcon.EarconPlayer
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.common.geometry.GeoUtils
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.location.LocationClient
import org.sightguide.core.sensors.compass.CompassSensorManager
import org.sightguide.feature.navigation.model.NavigationState

/**
 * Navigation ViewModel handling pedestrian walking directions,
 * sensor-fused compass heading, and accessibility audio/tactile guidance.
 */
class NavigationViewModel(
    private val locationClient: LocationClient,
    private val compassSensorManager: CompassSensorManager,
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager,
    private val earconPlayer: EarconPlayer = EarconPlayer()
) : ViewModel() {

    private val _state = MutableStateFlow(NavigationState())
    val state: StateFlow<NavigationState> = _state.asStateFlow()

    init {
        startSensors()
    }

    private fun startSensors() {
        compassSensorManager.startListening()

        viewModelScope.launch {
            compassSensorManager.headingDegrees.collect { heading ->
                _state.update { it.copy(currentHeadingDegrees = heading) }
                updateRelativeBearing()
            }
        }

        viewModelScope.launch {
            compassSensorManager.compassDirection.collect { direction ->
                _state.update { it.copy(compassDirection = direction) }
            }
        }

        viewModelScope.launch {
            locationClient.getLocationUpdates()
                .catch {
                    _state.update { it.copy(isGpsLost = true) }
                    ttsManager.speak("GPS signal lost. Please proceed with caution.")
                }
                .collect { coords ->
                    _state.update { it.copy(currentCoords = coords, isGpsLost = false) }
                    updateNavigationProgress()
                }
        }
    }

    fun startNavigation(destinationName: String, destinationCoords: Coordinates) {
        hapticManager.confirm()
        _state.update {
            it.copy(
                isNavigating = true,
                destinationName = destinationName,
                destinationCoords = destinationCoords,
                hasArrived = false
            )
        }
        updateNavigationProgress()
        ttsManager.speak("Starting walking route to $destinationName. Notice: SIGHTGUIDE is an assistive aid and does not guarantee obstacle avoidance.")
    }

    fun stopNavigation() {
        hapticManager.confirm()
        _state.update {
            it.copy(
                isNavigating = false,
                destinationName = null,
                destinationCoords = null,
                distanceRemainingMeters = 0.0,
                nextInstruction = "Navigation stopped"
            )
        }
        ttsManager.speak("Navigation stopped")
    }

    fun whereAmI() {
        hapticManager.confirm()
        viewModelScope.launch {
            val result = locationClient.getCurrentLocation()
            result.onSuccess { coords ->
                val direction = _state.value.compassDirection.spokenName
                val announcement = "You are at coordinates ${String.format("%.4f", coords.latitude)}, ${String.format("%.4f", coords.longitude)}, facing $direction."
                ttsManager.speak(announcement)
            }.onError { error ->
                ttsManager.speak(error.userFriendlyMessage)
            }
        }
    }

    fun announceDirection() {
        hapticManager.confirm()
        val dir = _state.value.compassDirection.spokenName
        val degrees = _state.value.currentHeadingDegrees.toInt()
        ttsManager.speak("Facing $dir, $degrees degrees")
    }

    fun repeatLastInstruction() {
        hapticManager.confirm()
        val instruction = _state.value.nextInstruction
        ttsManager.speak(instruction)
    }

    private fun updateNavigationProgress() {
        val current = _state.value.currentCoords ?: return
        val target = _state.value.destinationCoords ?: return

        val distance = GeoUtils.distanceMeters(current, target)
        val bearing = GeoUtils.initialBearingDegrees(current, target)
        val clock = GeoUtils.relativeClockDirection(_state.value.currentHeadingDegrees, bearing)

        if (distance <= 8.0 && !_state.value.hasArrived) {
            _state.update {
                it.copy(
                    hasArrived = true,
                    isNavigating = false,
                    distanceRemainingMeters = 0.0,
                    nextInstruction = "You have arrived at ${_state.value.destinationName}"
                )
            }
            hapticManager.confirm()
            earconPlayer.playWaypointReached()
            ttsManager.speak("You have arrived at ${_state.value.destinationName}")
            return
        }

        val distanceText = GeoUtils.formatSpokenDistance(distance)
        val instruction = "Target is $distanceText, ${clock.spokenDescription}"

        _state.update {
            it.copy(
                distanceRemainingMeters = distance,
                relativeClockDirection = clock,
                nextInstruction = instruction
            )
        }
    }

    private fun updateRelativeBearing() {
        val current = _state.value.currentCoords ?: return
        val target = _state.value.destinationCoords ?: return

        val bearing = GeoUtils.initialBearingDegrees(current, target)
        val clock = GeoUtils.relativeClockDirection(_state.value.currentHeadingDegrees, bearing)

        _state.update {
            it.copy(relativeClockDirection = clock)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compassSensorManager.stopListening()
    }
}
