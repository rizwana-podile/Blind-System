package org.sightguide.feature.navigation.viewmodel

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.common.model.CompassDirection
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.common.result.AppResult
import org.sightguide.core.location.LocationClient
import org.sightguide.core.sensors.compass.CompassSensorManager

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val locationClient: LocationClient = mockk(relaxed = true)
    private val compassSensorManager: CompassSensorManager = mockk(relaxed = true)
    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)

    private lateinit var viewModel: NavigationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { compassSensorManager.headingDegrees } returns flowOf(0f)
        every { compassSensorManager.compassDirection } returns flowOf(CompassDirection.NORTH)
        every { locationClient.getLocationUpdates() } returns flowOf(Coordinates(37.7749, -122.4194))

        viewModel = NavigationViewModel(
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
    fun startNavigation_setsNavigatingStateAndAnnounces() {
        val destCoords = Coordinates(37.7755, -122.4194)
        viewModel.startNavigation("Pharmacy", destCoords)

        val state = viewModel.state.value
        assertTrue(state.isNavigating)
        assertEquals("Pharmacy", state.destinationName)
        verify { hapticManager.confirm() }
    }

    @Test
    fun stopNavigation_resetsState() {
        viewModel.startNavigation("Pharmacy", Coordinates(37.7755, -122.4194))
        viewModel.stopNavigation()

        val state = viewModel.state.value
        assertFalse(state.isNavigating)
        assertEquals(0.0, state.distanceRemainingMeters, 0.01)
        verify { ttsManager.speak("Navigation stopped") }
    }

    @Test
    fun announceDirection_speaksCurrentHeading() {
        viewModel.announceDirection()

        verify { hapticManager.confirm() }
        verify { ttsManager.speak("Facing North, 0 degrees") }
    }

    @Test
    fun whereAmI_speaksCoordinates() {
        coEvery { locationClient.getCurrentLocation() } returns AppResult.Success(
            Coordinates(37.7749, -122.4194)
        )

        viewModel.whereAmI()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { hapticManager.confirm() }
    }
}
