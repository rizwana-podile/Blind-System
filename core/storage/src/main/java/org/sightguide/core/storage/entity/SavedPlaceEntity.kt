package org.sightguide.core.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-saved destinations (e.g. "Home", "Work", "Pharmacy", "Library")
 * with custom voice tags enabling quick voice commands ("Navigate to my clinic").
 */
@Entity(tableName = "saved_places")
data class SavedPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val label: String,
    val customVoiceTag: String, // Normalized lowercase token for voice matcher
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val category: String, // HOME, WORK, MEDICAL, TRANSIT, FAVORITE
    val frequencyCount: Int = 0,
    val lastVisitedMillis: Long = System.currentTimeMillis()
)
