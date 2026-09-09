package org.sightguide.feature.reader.viewmodel

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.earcon.EarconPlayer
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.feature.reader.model.DocumentType
import org.sightguide.feature.reader.model.ReadingDocument

class ReaderViewModelTest {

    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)
    private val earconPlayer: EarconPlayer = mockk(relaxed = true)
    private lateinit var viewModel: ReaderViewModel

    @Before
    fun setUp() {
        viewModel = ReaderViewModel(
            ttsManager = ttsManager,
            hapticManager = hapticManager,
            earconPlayer = earconPlayer
        )
    }

    @Test
    fun readNow_withDocument_initiatesReading() {
        val doc = ReadingDocument(
            rawText = "Please ring bell for assistance",
            documentType = DocumentType.SIGN,
            textBlocks = listOf("Please ring bell for assistance")
        )
        viewModel.onDocumentScanned(doc)
        viewModel.readNow()

        val state = viewModel.uiState.value
        assertTrue(state.isReading)
        verify { hapticManager.confirm() }
        verify { earconPlayer.playOcrSuccess() }
        verify { ttsManager.speak(match { it.contains("Please ring bell for assistance") }) }
    }

    @Test
    fun pauseReading_stopsTtsAndSetsPaused() {
        val doc = ReadingDocument("Content", DocumentType.GENERAL_DOCUMENT, listOf("Content"))
        viewModel.onDocumentScanned(doc)
        viewModel.readNow()
        viewModel.pauseReading()

        val state = viewModel.uiState.value
        assertFalse(state.isReading)
        assertTrue(state.isPaused)
        verify { ttsManager.stop() }
    }

    @Test
    fun stopReading_stopsPlaybackAndResetsState() {
        viewModel.stopReading()

        val state = viewModel.uiState.value
        assertFalse(state.isReading)
        assertFalse(state.isPaused)
        verify { ttsManager.speak("Reading stopped.") }
    }
}
