package org.sightguide.core.common.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CompassDirectionTest {

    @Test
    fun fromAzimuth_zeroDegrees_returnsNorth() {
        assertEquals(CompassDirection.NORTH, CompassDirection.fromAzimuth(0f))
        assertEquals(CompassDirection.NORTH, CompassDirection.fromAzimuth(359f))
        assertEquals(CompassDirection.NORTH, CompassDirection.fromAzimuth(10f))
    }

    @Test
    fun fromAzimuth_ninetyDegrees_returnsEast() {
        assertEquals(CompassDirection.EAST, CompassDirection.fromAzimuth(90f))
    }

    @Test
    fun fromAzimuth_oneEightyDegrees_returnsSouth() {
        assertEquals(CompassDirection.SOUTH, CompassDirection.fromAzimuth(180f))
    }

    @Test
    fun fromAzimuth_twoSeventyDegrees_returnsWest() {
        assertEquals(CompassDirection.WEST, CompassDirection.fromAzimuth(270f))
    }

    @Test
    fun fromAzimuth_intercardinalPoints_returnsCorrectDirection() {
        assertEquals(CompassDirection.NORTH_EAST, CompassDirection.fromAzimuth(45f))
        assertEquals(CompassDirection.SOUTH_EAST, CompassDirection.fromAzimuth(135f))
        assertEquals(CompassDirection.SOUTH_WEST, CompassDirection.fromAzimuth(225f))
        assertEquals(CompassDirection.NORTH_WEST, CompassDirection.fromAzimuth(315f))
    }

    @Test
    fun fromAzimuth_negativeDegrees_normalizesCorrectly() {
        assertEquals(CompassDirection.WEST, CompassDirection.fromAzimuth(-90f))
        assertEquals(CompassDirection.SOUTH, CompassDirection.fromAzimuth(-180f))
    }
}
