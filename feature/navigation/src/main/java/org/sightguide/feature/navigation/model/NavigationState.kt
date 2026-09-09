package org.sightguide.feature.navigation.model

import org.sightguide.core.common.geometry.ClockPosition
import org.sightguide.core.common.model.CompassDirection
import org.sightguide.core.common.model.Coordinates

/**
 * State representing active pedestrian walking guidance.
 */
data class NavigationState(
    val isNavigating: Boolean = false,
    val destinationName: String? = null,
    val destinationCoords: Coordinates? = null,
    val currentCoords: Coordinates? = null,
    val currentHeadingDegrees: Float = 0f,
    val compassDirection: CompassDirection = CompassDirection.NORTH,
    val distanceRemainingMeters: Double = 0.0,
    val relativeClockDirection: ClockPosition = ClockPosition.TWELVE_O_CLOCK,
    val nextInstruction: String = "Select destination or ask 'Where am I?'",
    val hasArrived: Boolean = false,
    val isGpsLost: Boolean = false
)
