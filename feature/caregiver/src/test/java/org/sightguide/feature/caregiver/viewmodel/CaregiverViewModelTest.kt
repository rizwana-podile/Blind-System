package org.sightguide.feature.caregiver.viewmodel

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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.storage.dao.AuditTrailDao
import org.sightguide.core.storage.datastore.UserSettingsDataStore

@OptIn(ExperimentalCoroutinesApi::class)
class CaregiverViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userSettingsDataStore: UserSettingsDataStore = mockk(relaxed = true)
    private val auditTrailDao: AuditTrailDao = mockk(relaxed = true)
    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)

    private lateinit var viewModel: CaregiverViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { userSettingsDataStore.isLocationSharingActive } returns flowOf(false)
        every { auditTrailDao.getAllLogs() } returns flowOf(emptyList())

        viewModel = CaregiverViewModel(
            userSettingsDataStore = userSettingsDataStore,
            auditTrailDao = auditTrailDao,
            ttsManager = ttsManager,
            hapticManager = hapticManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun generateNewPairingCode_createsCodeAndAnnounces() {
        viewModel.generateNewPairingCode()

        val code = viewModel.state.value.activePairingCode
        assertNotNull(code)
        assertEquals(7, code!!.length)
        assertTrue(code.contains('-'))

        verify { hapticManager.confirm() }
        verify { ttsManager.speak(match { it.contains("caregiver pairing code") }) }
    }
}
