package org.sightguide.feature.safety.viewmodel

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.location.LocationClient
import org.sightguide.core.storage.dao.EmergencyContactDao
import org.sightguide.core.storage.datastore.UserSettingsDataStore
import org.sightguide.core.storage.entity.EmergencyContactEntity
import org.sightguide.feature.safety.dispatcher.EmergencyDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class SafetyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val contactDao: EmergencyContactDao = mockk(relaxed = true)
    private val emergencyDispatcher: EmergencyDispatcher = mockk(relaxed = true)
    private val locationClient: LocationClient = mockk(relaxed = true)
    private val userSettingsDataStore: UserSettingsDataStore = mockk(relaxed = true)
    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)

    private lateinit var viewModel: SafetyViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val contacts = listOf(
            EmergencyContactEntity(1L, "Jane Doe", "555-0199", "Sister", isPrimary = true)
        )
        every { contactDao.getAllContacts() } returns flowOf(contacts)
        every { userSettingsDataStore.isLocationSharingActive } returns flowOf(false)

        viewModel = SafetyViewModel(
            contactDao = contactDao,
            emergencyDispatcher = emergencyDispatcher,
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
    fun triggerSos_startsCountdownAndSpeaksWarning() {
        viewModel.triggerSos()

        val state = viewModel.state.value
        assertTrue(state.isSosCountingDown)
        verify { ttsManager.speak(match { it.contains("SOS activated") }) }
    }

    @Test
    fun cancelSos_stopsCountdownAndSpeaksCancellation() {
        viewModel.triggerSos()
        viewModel.cancelSos()

        val state = viewModel.state.value
        assertFalse(state.isSosCountingDown)
        verify { hapticManager.confirm() }
        verify { ttsManager.speak(match { it.contains("SOS cancelled") }) }
    }

    @Test
    fun callPrimaryContact_dialsConfiguredPrimary() {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.callPrimaryContact()

        verify { hapticManager.confirm() }
        verify { emergencyDispatcher.dialContact(match { it.name == "Jane Doe" }) }
    }
}
