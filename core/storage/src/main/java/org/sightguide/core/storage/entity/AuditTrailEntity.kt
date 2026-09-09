package org.sightguide.core.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Immutable audit log guaranteeing complete transparency and user control
 * over emergency alerts and caregiver location sharing events.
 */
@Entity(tableName = "audit_trail")
data class AuditTrailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestampMillis: Long = System.currentTimeMillis(),
    val eventType: String, // SOS_TRIGGERED, SOS_CANCELLED, LOCATION_SHARED, SHARING_REVOKED
    val eventDetails: String,
    val integrityHash: String // SHA-256 validation token
)
