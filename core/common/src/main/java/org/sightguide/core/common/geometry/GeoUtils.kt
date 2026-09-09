package org.sightguide.core.common.geometry

import org.sightguide.core.common.model.Coordinates
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Geometric and spatial utility functions tailored for pedestrian guidance.
 */
object GeoUtils {

    private const val EARTH_RADIUS_METERS = 6371000.0
    private const val AVERAGE_WALKING_SPEED_MPS = 1.35 // ~4.8 km/h standard pedestrian pace

    /**
     * Computes the great-circle distance between two coordinates using the Haversine formula.
     * Returns distance in meters.
     */
    fun distanceMeters(from: Coordinates, to: Coordinates): Double {
        val latDistance = Math.toRadians(to.latitude - from.latitude)
        val lonDistance = Math.toRadians(to.longitude - from.longitude)

        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculates the initial bearing from point [from] to point [to] in degrees (0..360).
     */
    fun initialBearingDegrees(from: Coordinates, to: Coordinates): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lat2 = Math.toRadians(to.latitude)
        val deltaLon = Math.toRadians(to.longitude - from.longitude)

        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)

        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        return ((bearingDeg % 360f + 360f) % 360f).toFloat()
    }

    /**
     * Calculates the clock-face relative direction based on the user's current heading
     * and the bearing to the destination (e.g., "12 o'clock" means directly ahead, "3 o'clock" means directly to your right).
     * This clock-face representation is recognized globally as the gold standard for blind navigation.
     */
    fun relativeClockDirection(userHeadingDegrees: Float, targetBearingDegrees: Float): ClockPosition {
        val relativeAngle = ((targetBearingDegrees - userHeadingDegrees) % 360f + 360f) % 360f
        val hourNumber = when {
            relativeAngle >= 345f || relativeAngle < 15f -> 12
            relativeAngle < 45f -> 1
            relativeAngle < 75f -> 2
            relativeAngle < 105f -> 3
            relativeAngle < 135f -> 4
            relativeAngle < 165f -> 5
            relativeAngle < 195f -> 6
            relativeAngle < 225f -> 7
            relativeAngle < 255f -> 8
            relativeAngle < 285f -> 9
            relativeAngle < 315f -> 10
            relativeAngle < 345f -> 11
            else -> 12
        }
        return ClockPosition.fromHour(hourNumber)
    }

    /**
     * Estimates remaining walking time in seconds based on distance in meters.
     */
    fun estimateWalkingDurationSeconds(distanceMeters: Double): Int {
        if (distanceMeters <= 0.0) return 0
        return (distanceMeters / AVERAGE_WALKING_SPEED_MPS).toInt()
    }

    /**
     * Formats distance into accessible spoken text (e.g. "50 meters", "1.2 kilometers").
     */
    fun formatSpokenDistance(distanceMeters: Double): String {
        return when {
            distanceMeters < 10.0 -> "Just ahead"
            distanceMeters < 1000.0 -> "${distanceMeters.toInt()} meters"
            else -> {
                val km = distanceMeters / 1000.0
                String.format("%.1f kilometers", km)
            }
        }
    }
}

enum class ClockPosition(val hour: Int, val spokenDescription: String) {
    TWELVE_O_CLOCK(12, "Directly ahead at 12 o'clock"),
    ONE_O_CLOCK(1, "Slightly right at 1 o'clock"),
    TWO_O_CLOCK(2, "Ahead to your right at 2 o'clock"),
    THREE_O_CLOCK(3, "Directly to your right at 3 o'clock"),
    FOUR_O_CLOCK(4, "Behind to your right at 4 o'clock"),
    FIVE_O_CLOCK(5, "Behind to your right at 5 o'clock"),
    SIX_O_CLOCK(6, "Directly behind you at 6 o'clock"),
    SEVEN_O_CLOCK(7, "Behind to your left at 7 o'clock"),
    EIGHT_O_CLOCK(8, "Behind to your left at 8 o'clock"),
    NINE_O_CLOCK(9, "Directly to your left at 9 o'clock"),
    TEN_O_CLOCK(10, "Ahead to your left at 10 o'clock"),
    ELEVEN_O_CLOCK(11, "Slightly left at 11 o'clock");

    companion object {
        fun fromHour(hour: Int): ClockPosition {
            return entries.find { it.hour == hour } ?: TWELVE_O_CLOCK
        }
    }
}
