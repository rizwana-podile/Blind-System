package org.sightguide.core.sensors.compass

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassSensorMathTest {

    @Test
    fun circularAngleSmoothing_acrossDiscontinuity_smoothsSmoothly() {
        // Angle 1 = 359 degrees (-1 deg), Angle 2 = 1 degree
        val rad1 = Math.toRadians(359.0)
        val rad2 = Math.toRadians(1.0)

        // Vector average
        val avgSin = (sin(rad1) + sin(rad2)) / 2.0
        val avgCos = (cos(rad1) + cos(rad2)) / 2.0

        val avgRad = atan2(avgSin, avgCos)
        var avgDeg = Math.toDegrees(avgRad)
        avgDeg = (avgDeg + 360.0) % 360.0

        // The smoothed angle between 359° and 1° must be 0° (North), NOT 180°!
        assertTrue("Expected average around 0 degrees, but was $avgDeg", avgDeg < 0.1 || avgDeg > 359.9)
    }

    @Test
    fun sensorAccuracy_hasHumanFriendlySpokenAdvice() {
        assertEquals("Compass calibrated", SensorAccuracy.HIGH.spokenAdvice)
        assertTrue(SensorAccuracy.LOW.spokenAdvice.contains("figure-8"))
        assertTrue(SensorAccuracy.UNRELIABLE.spokenAdvice.contains("figure-8"))
    }
}
