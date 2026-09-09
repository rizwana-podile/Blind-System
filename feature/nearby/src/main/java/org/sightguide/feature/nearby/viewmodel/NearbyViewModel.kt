package org.sightguide.feature.nearby.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.location.LocationClient
import org.sightguide.core.sensors.compass.CompassSensorManager
import org.sightguide.core.storage.entity.AmenityCategory
import org.sightguide.feature.nearby.model.NearbyPlaceItem
import org.sightguide.feature.nearby.repository.NearbyPlacesRepository

/**
 * ViewModel managing nearby essential amenity search, filters, and auditory announcements.
 */
class NearbyViewModel(
    private val repository: NearbyPlacesRepository,
    private val locationClient: LocationClient,
    private val compassSensorManager: CompassSensorManager,
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager
) : ViewModel() {

    data class NearbyUiState(
        val places: List<NearbyPlaceItem> = emptyList(),
        val selectedCategory: AmenityCategory? = null,
        val isLoading: Boolean = false,
        val statusMessage: String = "Searching nearby places..."
    )

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState: StateFlow<NearbyUiState> = _uiState.asStateFlow()

    init {
        searchNearby(null)
    }

    fun searchNearby(category: AmenityCategory?) {
        hapticManager.confirm()
        _uiState.update { it.copy(selectedCategory = category, isLoading = true) }

        viewModelScope.launch {
            val locationResult = locationClient.getCurrentLocation()
            val coords = locationResult.getOrNull() ?: Coordinates(37.7749, -122.4194)
            val heading = compassSensorManager.headingDegrees.value

            val results = repository.findNearby(coords, heading, category)
            val summary = if (results.isEmpty()) {
                val catName = category?.spokenLabel ?: "amenities"
                "No $catName found nearby in the local offline database."
            } else {
                val nearest = results.first()
                "Found ${results.size} places. Nearest is ${nearest.spokenDescription}."
            }

            _uiState.update {
                it.copy(
                    places = results,
                    isLoading = false,
                    statusMessage = summary
                )
            }

            ttsManager.speak(summary)
        }
    }

    fun onPlaceClicked(item: NearbyPlaceItem, onNavigate: (NearbyPlaceItem) -> Unit) {
        hapticManager.confirm()
        ttsManager.speak("Selected ${item.name}. ${item.address}. Starting navigation.")
        onNavigate(item)
    }
}
