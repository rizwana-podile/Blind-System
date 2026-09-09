package org.sightguide.core.common.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sightguide.core.common.model.Coordinates

class GeoUtilsTest {

    @Test
    fun distanceMeters_calculatesAccurateDistance() {
        // Empire State Building to Times Square (~1.4 km)
        val empireState = Coordinates(40.7484, -73.9857)
        val timesSquare = Coordinates(40.7580, -73.9855)

        val distance = GeoUtils.distanceMeters(empireState, timesSquare)

        // Distance should be ~1068 meters
        assertTrue("Distance should be approximately 1068m, but was $distance", distance in 1000.0..1150.0)
    }

    @Test
    fun initialBearing_dueNorth_returnsZeroOrNearZero() {
        val pointA = Coordinates(0.0, 0.0)
        val pointB = Coordinates(1.0, 0.0) // Due North

        val bearing = GeoUtils.initialBearingDegrees(pointA, pointB)

        assertEquals(0.0f, bearing, 0.1f)
    }

    @Test
    fun initialBearing_dueEast_returns90Degrees() {
        val pointA = Coordinates(0.0, 0.0)
        val pointB = Coordinates(0.0, 1.0) // Due East

        val bearing = GeoUtils.initialBearingDegrees(pointA, pointB)

        assertEquals(90.0f, bearing, 0.5f)
    }

    @Test
    fun relativeClockDirection_headingNorthTargetEast_returns3OClock() {
        val userHeading = 0.0f // Facing North
        val targetBearing = 90.0f // Target is East

        val clock = GeoUtils.relativeClockDirection(userHeading, targetBearing)

        assertEquals(ClockPosition.THREE_O_CLOCK, clock)
        assertEquals("Directly to your right at 3 o'clock", clock.spokenDescription)
    }

    @Test
    fun relativeClockDirection_headingNorthTargetNorth_returns12OClock() {
        val userHeading = 0.0f // Facing North
        val targetBearing = 5.0f // Target is nearly straight ahead

        val clock = GeoUtils.relativeClockDirection(userHeading, targetBearing)

        assertEquals(ClockPosition.TWELVE_O_CLOCK, clock)
    }

    @Test
    fun estimateWalkingDurationSeconds_calculatesExpectedPace() {
        val distance = 135.0 // 135 meters at 1.35 m/s = 100 seconds
        val duration = GeoUtils.estimateWalkingDurationSeconds(distance)

        assertEquals(100, duration)
    }

    @Test
    fun formatSpokenDistance_formatsSmallAndLargeDistances() {
        assertEquals("Just ahead", GeoUtils.formatSpokenDistance(5.0))
        assertEquals("150 meters", GeoUtils.formatSpokenDistance(150.0))
        assertEquals("1.5 kilometers", GeoUtils.formatSpokenDistance(1500.0))
    }
}
