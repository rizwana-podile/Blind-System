package org.sightguide.feature.safety.model

import org.sightguide.core.storage.entity.EmergencyContactEntity

/**
 * State representing emergency safety status, contacts, and active SOS countdown.
 */
data class SafetyState(
    val isSosCountingDown: Boolean = false,
    val countdownSecondsRemaining: Int = 5,
    val isLocationSharingActive: Boolean = false,
    val primaryContact: EmergencyContactEntity? = null,
    val contacts: List<EmergencyContactEntity> = emptyList(),
    val isSosDispatched: Boolean = false,
    val statusNotice: String = "Emergency dashboard ready. Long-press or tap SOS in an emergency."
)
