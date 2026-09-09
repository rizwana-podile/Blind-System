package org.sightguide.feature.caregiver.model

import org.sightguide.core.storage.entity.AuditTrailEntity

/**
 * State representing caregiver consent settings, active pairing code, and audit trail logs.
 */
data class CaregiverState(
    val isSharingActive: Boolean = false,
    val pairedCaregiverId: String? = null,
    val activePairingCode: String? = null,
    val auditLogs: List<AuditTrailEntity> = emptyList(),
    val statusMessage: String = "Caregiver consent center ready. All telemetry is under your control."
)
