package org.sightguide.feature.nearby.viewmodel

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.common.geometry.ClockPosition
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.common.result.AppResult
import org.sightguide.core.location.LocationClient
import org.sightguide.core.sensors.compass.CompassSensorManager
import org.sightguide.core.storage.entity.AmenityCategory
import org.sightguide.feature.nearby.model.NearbyPlaceItem
import org.sightguide.feature.nearby.repository.NearbyPlacesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class NearbyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: NearbyPlacesRepository = mockk(relaxed = true)
    private val locationClient: LocationClient = mockk(relaxed = true)
    private val compassSensorManager: CompassSensorManager = mockk(relaxed = true)
    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)

    private lateinit var viewModel: NearbyViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { locationClient.getCurrentLocation() } returns AppResult.Success(Coordinates(37.7749, -122.4194))
        every { compassSensorManager.headingDegrees } returns MutableStateFlow(0f)

        viewModel = NearbyViewModel(
            repository = repository,
            locationClient = locationClient,
            compassSensorManager = compassSensorManager,
            ttsManager = ttsManager,
            hapticManager = hapticManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchNearby_queriesRepositoryAndSpeaks() {
        val sampleItem = NearbyPlaceItem(
            id = 1L,
            name = "Test Pharmacy",
            category = AmenityCategory.PHARMACY,
            distanceMeters = 100.0,
            clockPosition = ClockPosition.TWELVE_O_CLOCK,
            coordinates = Coordinates(37.7755, -122.4194),
            address = "123 Main St"
        )
        coEvery { repository.findNearby(any(), any(), any()) } returns listOf(sampleItem)

        viewModel.searchNearby(AmenityCategory.PHARMACY)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.places.size)
        assertEquals(AmenityCategory.PHARMACY, state.selectedCategory)
        verify { hapticManager.confirm() }
        verify { ttsManager.speak(match { it.contains("Test Pharmacy") }) }
    }
}
