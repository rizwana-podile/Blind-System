package org.sightguide.feature.safety.dispatcher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.security.CaregiverTokenManager
import org.sightguide.core.storage.dao.AuditTrailDao
import org.sightguide.core.storage.entity.AuditTrailEntity
import org.sightguide.core.storage.entity.EmergencyContactEntity

/**
 * Handles physical emergency dispatch actions (phone calls, SMS location broadcasts)
 * and records signed audit logs for legal/privacy integrity.
 */
class EmergencyDispatcher(
    private val context: Context,
    private val auditTrailDao: AuditTrailDao
) {

    fun dialContact(contact: EmergencyContactEntity): Boolean {
        val phoneUri = Uri.parse("tel:${contact.phoneNumber}")
        val callIntent = Intent(Intent.ACTION_CALL, phoneUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(callIntent)
            true
        } catch (_: SecurityException) {
            // Fallback to system dialer if direct CALL_PHONE permission is denied
            val dialIntent = Intent(Intent.ACTION_DIAL, phoneUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(dialIntent)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    suspend fun dispatchSosSms(
        contacts: List<EmergencyContactEntity>,
        currentCoords: Coordinates?
    ): Boolean {
        val message = buildSosMessage(currentCoords)
        var dispatchedAtLeastOne = false

        val smsManager: SmsManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        for (contact in contacts) {
            if (!contact.canReceiveSms) continue

            try {
                smsManager?.sendTextMessage(contact.phoneNumber, null, message, null, null)
                dispatchedAtLeastOne = true
            } catch (_: SecurityException) {
                // Ignore if SMS permission is absent
            }
        }

        // Record immutable audit log
        val payload = "SOS_DISPATCHED|${System.currentTimeMillis()}|Coords($currentCoords)"
        val signature = CaregiverTokenManager.calculateHash(payload)
        auditTrailDao.insertLog(
            AuditTrailEntity(
                eventType = "SOS_DISPATCHED",
                eventDetails = "SOS alert dispatched to ${contacts.size} contacts. Location: ${currentCoords?.latitude}, ${currentCoords?.longitude}",
                integrityHash = signature
            )
        )

        return dispatchedAtLeastOne
    }

    suspend fun recordSosCancelled() {
        val payload = "SOS_CANCELLED|${System.currentTimeMillis()}"
        val signature = CaregiverTokenManager.calculateHash(payload)
        auditTrailDao.insertLog(
            AuditTrailEntity(
                eventType = "SOS_CANCELLED",
                eventDetails = "SOS countdown cancelled by user before dispatch",
                integrityHash = signature
            )
        )
    }

    fun buildSosMessage(coords: Coordinates?): String {
        return if (coords != null) {
            "SIGHTGUIDE SOS Alert: I need assistance. My current location is https://maps.google.com/?q=${coords.latitude},${coords.longitude}"
        } else {
            "SIGHTGUIDE SOS Alert: I need assistance. GPS coordinates were unavailable at time of alert."
        }
    }
}
