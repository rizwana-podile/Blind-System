package org.sightguide.feature.nearby.model

import org.sightguide.core.common.geometry.ClockPosition
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.storage.entity.AmenityCategory

/**
 * Item representing a discovered nearby essential place with distance and relative direction.
 */
data class NearbyPlaceItem(
    val id: Long,
    val name: String,
    val category: AmenityCategory,
    val distanceMeters: Double,
    val clockPosition: ClockPosition,
    val coordinates: Coordinates,
    val address: String,
    val openingHours: String = ""
) {
    val spokenDescription: String
        get() {
            val dist = if (distanceMeters < 1000.0) {
                "${distanceMeters.toInt()} meters"
            } else {
                "${String.format("%.1f", distanceMeters / 1000.0)} kilometers"
            }
            return "$name, $dist away, ${clockPosition.spokenDescription}"
        }
}
