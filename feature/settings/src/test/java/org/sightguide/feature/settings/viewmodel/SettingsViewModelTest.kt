package org.sightguide.feature.settings.viewmodel

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
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.earcon.EarconPlayer
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.storage.datastore.UserSettingsDataStore

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userSettingsDataStore: UserSettingsDataStore = mockk(relaxed = true)
    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)
    private val earconPlayer: EarconPlayer = mockk(relaxed = true)

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { userSettingsDataStore.speechRate } returns flowOf(1.2f)
        every { userSettingsDataStore.speechPitch } returns flowOf(1.0f)
        every { userSettingsDataStore.isHighContrastTheme } returns flowOf(true)
        every { userSettingsDataStore.hapticLevel } returns flowOf(2)
        every { userSettingsDataStore.isAudioChimesEnabled } returns flowOf(true)
        every { userSettingsDataStore.sosCountdownSeconds } returns flowOf(5)

        viewModel = SettingsViewModel(
            userSettingsDataStore = userSettingsDataStore,
            ttsManager = ttsManager,
            hapticManager = hapticManager,
            earconPlayer = earconPlayer
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testVoiceSpeech_speaksSampleText() {
        viewModel.testVoiceSpeech()

        verify { hapticManager.confirm() }
        verify { ttsManager.speak(match { it.contains("sample of SIGHTGUIDE") }) }
    }

    @Test
    fun setSpeechRate_updatesDataStore() {
        coEvery { userSettingsDataStore.setSpeechRate(1.5f) } returns Unit

        viewModel.setSpeechRate(1.5f)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { ttsManager.setSpeechRate(any()) }
    }
}
