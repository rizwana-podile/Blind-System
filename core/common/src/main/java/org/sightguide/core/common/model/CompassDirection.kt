package org.sightguide.core.common.model

/**
 * 8-point compass orientation with localized spoken text
 * and angular mapping.
 */
enum class CompassDirection(
    val shortName: String,
    val spokenName: String,
    val minDegrees: Float,
    val maxDegrees: Float
) {
    NORTH("N", "North", 337.5f, 22.5f),
    NORTH_EAST("NE", "North-East", 22.5f, 67.5f),
    EAST("E", "East", 67.5f, 112.5f),
    SOUTH_EAST("SE", "South-East", 112.5f, 157.5f),
    SOUTH("S", "South", 157.5f, 202.5f),
    SOUTH_WEST("SW", "South-West", 202.5f, 247.5f),
    WEST("W", "West", 247.5f, 292.5f),
    NORTH_WEST("NW", "North-West", 292.5f, 337.5f);

    companion object {
        /**
         * Normalizes an azimuth angle (0..360) and returns the corresponding CompassDirection.
         */
        fun fromAzimuth(azimuthDegrees: Float): CompassDirection {
            val normalized = (azimuthDegrees % 360f + 360f) % 360f
            return when {
                normalized >= 337.5f || normalized < 22.5f -> NORTH
                normalized < 67.5f -> NORTH_EAST
                normalized < 112.5f -> EAST
                normalized < 157.5f -> SOUTH_EAST
                normalized < 202.5f -> SOUTH
                normalized < 247.5f -> SOUTH_WEST
                normalized < 292.5f -> WEST
                else -> NORTH_WEST
            }
        }
    }
}
