package org.sightguide.core.accessibility.haptics

import android.content.Context
import android.os.Vibrator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class HapticPatternTest {

    private val context: Context = mockk(relaxed = true)
    private val vibrator: Vibrator = mockk(relaxed = true)
    private lateinit var hapticPatternManager: HapticPatternManager

    @Before
    fun setUp() {
        every { context.getSystemService(Context.VIBRATOR_SERVICE) } returns vibrator
        every { vibrator.hasVibrator() } returns true
        hapticPatternManager = HapticPatternManager(context)
    }

    @Test
    fun cancel_invokesVibratorCancel() {
        hapticPatternManager.cancel()
        verify { vibrator.cancel() }
    }
}
