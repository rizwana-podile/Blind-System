package org.sightguide.core.location.filter

import kotlin.math.sqrt

/**
 * 1-D / 2-D Kalman filter customized for pedestrian walking dynamics.
 * Eliminates high-frequency GPS position jumping caused by tall buildings (urban canyons)
 * and standing drift.
 */
class PedestrianKalmanFilter(
    private val processNoiseSigma: Double = 3.0 // Walking pedestrian acceleration standard deviation (m/s^2)
) {

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var variance: Double = -1.0 // Uninitialized variance
    private var timestampMillis: Long = 0L

    val isInitialized: Boolean
        get() = variance >= 0.0

    fun reset() {
        variance = -1.0
        timestampMillis = 0L
    }

    /**
     * Filters a new raw measurement and returns the smoothed (lat, lng, estimatedAccuracyMeters).
     */
    fun filter(
        rawLat: Double,
        rawLng: Double,
        rawAccuracyMeters: Float,
        timeMillis: Long
    ): FilteredLocation {
        val measurementAccuracy = rawAccuracyMeters.coerceAtLeast(1.0f).toDouble()

        if (variance < 0.0) {
            // First measurement initialization
            lat = rawLat
            lng = rawLng
            variance = measurementAccuracy * measurementAccuracy
            timestampMillis = timeMillis
            return FilteredLocation(lat, lng, rawAccuracyMeters)
        }

        val deltaSeconds = (timeMillis - timestampMillis).coerceAtLeast(0L) / 1000.0
        timestampMillis = timeMillis

        // 1. Time update (Prediction step): variance increases with time based on motion process noise
        if (deltaSeconds > 0.0) {
            variance += deltaSeconds * processNoiseSigma * processNoiseSigma
        }

        // 2. Measurement update (Correction step): Kalman gain K
        val measurementVariance = measurementAccuracy * measurementAccuracy
        val kalmanGain = variance / (variance + measurementVariance)

        // Update state estimate
        lat += kalmanGain * (rawLat - lat)
        lng += kalmanGain * (rawLng - lng)

        // Update estimate error variance
        variance = (1.0 - kalmanGain) * variance

        val estimatedAccuracy = sqrt(variance).toFloat()
        return FilteredLocation(lat, lng, estimatedAccuracy)
    }

    data class FilteredLocation(
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Float
    )
}
