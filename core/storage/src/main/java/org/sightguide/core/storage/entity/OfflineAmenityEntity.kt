package org.sightguide.core.storage.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Essential point of interest stored locally for offline-first discovery.
 * Indexed by geohash prefix for near-instant geometric proximity queries.
 */
@Entity(
    tableName = "offline_amenities",
    indices = [
        Index(value = ["geohash"]),
        Index(value = ["category"])
    ]
)
data class OfflineAmenityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String, // PHARMACY, HOSPITAL, GROCERY, TRANSIT, BANK, POLICE, RESTAURANT
    val latitude: Double,
    val longitude: Double,
    val geohash: String, // 6 to 8 character geohash
    val streetAddress: String,
    val openingHours: String = "Hours not available offline",
    val phoneNumber: String? = null
)

enum class AmenityCategory(val spokenLabel: String) {
    PHARMACY("Pharmacy"),
    HOSPITAL("Hospital and Medical Clinic"),
    GROCERY("Grocery Store and Supermarket"),
    TRANSIT("Bus and Train Station"),
    BANK("Bank and ATM"),
    POLICE("Police and Emergency Station"),
    RESTAURANT("Restaurant and Food"),
    OTHER("Point of Interest");

    companion object {
        fun fromString(value: String): AmenityCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}
