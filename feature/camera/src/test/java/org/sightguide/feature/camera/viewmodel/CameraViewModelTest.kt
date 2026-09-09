package org.sightguide.feature.camera.viewmodel

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.feature.camera.model.ConfidenceLevel
import org.sightguide.feature.camera.model.DetectedObstacle
import org.sightguide.feature.camera.model.SpatialPosition

class CameraViewModelTest {

    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val hapticManager: HapticPatternManager = mockk(relaxed = true)
    private lateinit var viewModel: CameraViewModel

    @Before
    fun setUp() {
        viewModel = CameraViewModel(
            ttsManager = ttsManager,
            hapticManager = hapticManager
        )
    }

    @Test
    fun describeScene_emptyView_speaksClearPath() {
        viewModel.describeScene()

        verify { hapticManager.confirm() }
        verify { ttsManager.speak(match { it.contains("Path appears clear") }) }
    }

    @Test
    fun describeScene_withObstacles_speaksObstacleList() {
        val obstacle = DetectedObstacle(
            label = "Person",
            confidence = ConfidenceLevel.DETECTED,
            spatialPosition = SpatialPosition.CENTER,
            approximateDistanceMeters = 2.0f
        )
        viewModel.onObstaclesDetected(listOf(obstacle))
        viewModel.describeScene()

        verify { ttsManager.speak(match { it.contains("Person detected 2.0 meters directly ahead") }) }
    }

    @Test
    fun startSearch_setsTargetAndAnnounces() {
        viewModel.startSearch("bottle")

        val state = viewModel.uiState.value
        assertTrue(state.isSearching)
        assertEquals("bottle", state.searchTarget)
        verify { ttsManager.speak(match { it.contains("Searching for bottle") }) }
    }

    @Test
    fun stopSearch_resetsSearchState() {
        viewModel.startSearch("bottle")
        viewModel.stopSearch()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        verify { ttsManager.speak("Object search stopped") }
    }

    @Test
    fun toggleTorch_flipsTorchState() {
        assertFalse(viewModel.uiState.value.isTorchEnabled)
        viewModel.toggleTorch()
        assertTrue(viewModel.uiState.value.isTorchEnabled)
        viewModel.toggleTorch()
        assertFalse(viewModel.uiState.value.isTorchEnabled)
    }
}
