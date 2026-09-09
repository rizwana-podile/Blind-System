package org.sightguide.core.location.filter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PedestrianKalmanFilterTest {

    private lateinit var filter: PedestrianKalmanFilter

    @Before
    fun setUp() {
        filter = PedestrianKalmanFilter()
    }

    @Test
    fun initialMeasurement_initializesStateDirectly() {
        val filtered = filter.filter(
            rawLat = 37.7749,
            rawLng = -122.4194,
            rawAccuracyMeters = 10f,
            timeMillis = 1000L
        )

        assertTrue(filter.isInitialized)
        assertEquals(37.7749, filtered.latitude, 0.0001)
        assertEquals(-122.4194, filtered.longitude, 0.0001)
    }

    @Test
    fun noisyOutlierMeasurement_isDampenedByFilter() {
        // First valid reading
        filter.filter(
            rawLat = 37.7749,
            rawLng = -122.4194,
            rawAccuracyMeters = 5f,
            timeMillis = 1000L
        )

        // Noisy outlier with poor accuracy (30m accuracy spike)
        val filtered = filter.filter(
            rawLat = 37.7760, // ~120m away outlier
            rawLng = -122.4194,
            rawAccuracyMeters = 30f,
            timeMillis = 2000L
        )

        // The filtered latitude should be pulled slightly towards the outlier, but heavily dampened!
        assertTrue(
            "Filtered latitude should dampen outlier: got ${filtered.latitude}",
            filtered.latitude < 37.7755
        )
    }

    @Test
    fun reset_clearsFilterInitialization() {
        filter.filter(37.7749, -122.4194, 5f, 1000L)
        assertTrue(filter.isInitialized)

        filter.reset()
        assertTrue(!filter.isInitialized)
    }
}
