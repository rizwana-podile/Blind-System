package org.sightguide.core.audio.tts

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class TextToSpeechManagerTest {

    private val context: Context = mockk(relaxed = true)
    private lateinit var ttsManager: TextToSpeechManager

    @Before
    fun setUp() {
        ttsManager = TextToSpeechManager(context)
    }

    @Test
    fun instance_createsCleanly() {
        assertNotNull(ttsManager)
        assertNotNull(ttsManager.isInitialized)
        assertNotNull(ttsManager.isSpeaking)
    }
}
