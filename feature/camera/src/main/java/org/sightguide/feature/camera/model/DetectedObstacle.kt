package org.sightguide.feature.camera.model

/**
 * Representation of a detected physical entity in front of the user.
 * Explicitly models confidence levels to comply with assistive safety rules:
 * never presenting uncertain detections as absolute facts.
 */
data class DetectedObstacle(
    val label: String,
    val confidence: ConfidenceLevel,
    val spatialPosition: SpatialPosition,
    val approximateDistanceMeters: Float? = null,
    val isHazard: Boolean = false
) {
    val spokenDescription: String
        get() {
            val dist = if (approximateDistanceMeters != null) {
                "${String.format("%.1f", approximateDistanceMeters)} meters"
            } else {
                "nearby"
            }
            return when (confidence) {
                ConfidenceLevel.DETECTED -> "$label detected $dist ${spatialPosition.spokenPhrase}"
                ConfidenceLevel.PROBABLY_DETECTED -> "Probably a $label $dist ${spatialPosition.spokenPhrase}"
                ConfidenceLevel.UNCERTAIN -> "Possible object ${spatialPosition.spokenPhrase}"
            }
        }
}

enum class ConfidenceLevel {
    DETECTED,
    PROBABLY_DETECTED,
    UNCERTAIN
}

enum class SpatialPosition(val spokenPhrase: String) {
    LEFT("on your left"),
    CENTER("directly ahead"),
    RIGHT("to your right")
}
