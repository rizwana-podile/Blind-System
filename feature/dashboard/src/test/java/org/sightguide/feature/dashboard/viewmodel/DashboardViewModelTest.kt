package org.sightguide.feature.dashboard.viewmodel

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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.location.LocationClient
import org.sightguide.core.storage.datastore.UserSettingsDataStore
import org.sightguide.feature.dashboard.model.GpsStatus

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val locationClient: LocationClient = mockk(relaxed = true)
    private val userSettingsDataStore: UserSettingsDataStore = mockk(relaxed = true)
    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { locationClient.isLocationServiceEnabled() } returns true
        every { userSettingsDataStore.isLocationSharingActive } returns flowOf(false)

        viewModel = DashboardViewModel(
            locationClient = locationClient,
            userSettingsDataStore = userSettingsDataStore,
            ttsManager = ttsManager,
            hapticManager = hapticManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasActiveGpsStatus() {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertNotNull(state)
        assertEquals(GpsStatus.ACTIVE, state.gpsStatus)
    }

    @Test
    fun onDashboardOpened_speaksGreetingAndHaptic() {
        viewModel.onDashboardOpened()

        verify { hapticManager.confirm() }
        verify { ttsManager.speak("SIGHTGUIDE. What can I help with?") }
    }

    @Test
    fun onFeatureTileClicked_speaksDestinationAndHaptic() {
        viewModel.onFeatureTileClicked("Navigation", "Opening walking directions")

        verify { hapticManager.confirm() }
        verify { ttsManager.speak("Opening Navigation. Opening walking directions") }
    }
}
