package org.sightguide.core.common.model

/**
 * High-precision geospatial coordinate snapshot.
 * Includes accuracy metrics essential for blind pedestrian safety.
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double? = null,
    val accuracyMeters: Float = 0f,
    val speedMps: Float? = null,
    val bearingDegrees: Float? = null,
    val timestampMillis: Long = System.currentTimeMillis()
) {
    /**
     * Pedestrian accuracy classification.
     * High accuracy (< 5m) is suitable for sidewalk guidance;
     * Poor accuracy (> 20m) warrants caution in navigation prompts.
     */
    val accuracyQuality: AccuracyQuality
        get() = when {
            accuracyMeters <= 0f -> AccuracyQuality.UNKNOWN
            accuracyMeters <= 5f -> AccuracyQuality.HIGH_SIDEWALK
            accuracyMeters <= 15f -> AccuracyQuality.MODERATE_STREET
            else -> AccuracyQuality.LOW_CAUTION
        }

    enum class AccuracyQuality(val spokenDescription: String) {
        HIGH_SIDEWALK("High precision GPS signal"),
        MODERATE_STREET("Standard street accuracy"),
        LOW_CAUTION("Low accuracy GPS. Please proceed with caution"),
        UNKNOWN("Acquiring GPS location")
    }
}
