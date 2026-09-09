package org.sightguide.core.storage.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeohashUtilsTest {

    @Test
    fun encode_standardCoordinates_returnsExpectedGeohash() {
        // Sydney Opera House coordinates (~ -33.8568, 151.2153) -> "r3gx2"
        val geohash = GeohashUtils.encode(-33.8568, 151.2153, precision = 6)
        assertTrue(geohash.startsWith("r3gx2"))
    }

    @Test
    fun encode_nearbyPoints_shareCommonPrefix() {
        // Points 50 meters apart
        val hash1 = GeohashUtils.encode(37.77490, -122.41940, precision = 7)
        val hash2 = GeohashUtils.encode(37.77495, -122.41945, precision = 7)

        // They must share at least the first 5 characters (sub-kilometer resolution)
        assertEquals(hash1.substring(0, 5), hash2.substring(0, 5))
    }
}
