package org.sightguide.core.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Trusted contact configured for one-tap or voice-activated emergency calling and SMS dispatch.
 */
@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isPrimary: Boolean = false,
    val canReceiveSms: Boolean = true,
    val canReceiveCall: Boolean = true
)
