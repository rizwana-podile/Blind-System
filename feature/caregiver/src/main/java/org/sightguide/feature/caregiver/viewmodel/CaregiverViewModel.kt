package org.sightguide.feature.caregiver.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.security.CaregiverTokenManager
import org.sightguide.core.storage.dao.AuditTrailDao
import org.sightguide.core.storage.datastore.UserSettingsDataStore
import org.sightguide.core.storage.entity.AuditTrailEntity
import org.sightguide.feature.caregiver.model.CaregiverState

/**
 * ViewModel managing consent-based caregiver pairing and audit inspection.
 */
class CaregiverViewModel(
    private val userSettingsDataStore: UserSettingsDataStore,
    private val auditTrailDao: AuditTrailDao,
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager
) : ViewModel() {

    private val _state = MutableStateFlow(CaregiverState())
    val state: StateFlow<CaregiverState> = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            userSettingsDataStore.isLocationSharingActive.collect { active ->
                _state.update { it.copy(isSharingActive = active) }
            }
        }

        viewModelScope.launch {
            auditTrailDao.getAllLogs().collect { logs ->
                _state.update { it.copy(auditLogs = logs) }
            }
        }
    }

    fun generateNewPairingCode() {
        hapticManager.confirm()
        val code = CaregiverTokenManager.generatePairingCode()
        _state.update {
            it.copy(
                activePairingCode = code,
                statusMessage = "Generated temporary pairing code: $code."
            )
        }

        val spokenCode = code.replace("-", " dash ").toCharArray().joinToString(" ")
        ttsManager.speak("Your temporary caregiver pairing code is $spokenCode. Share this in person with your trusted caregiver.")
    }

    fun toggleLocationSharing() {
        hapticManager.confirm()
        val next = !_state.value.isSharingActive
        viewModelScope.launch {
            userSettingsDataStore.setLocationSharingActive(next)

            val event = if (next) "SHARING_ENABLED" else "SHARING_REVOKED"
            val signature = CaregiverTokenManager.calculateHash("$event|${System.currentTimeMillis()}")
            auditTrailDao.insertLog(
                AuditTrailEntity(
                    eventType = event,
                    eventDetails = "User manually toggled location sharing to: $next",
                    integrityHash = signature
                )
            )

            val msg = if (next) {
                "Location sharing active. Caregiver can view your location."
            } else {
                "Location sharing stopped. No caregiver can view your position."
            }
            ttsManager.speak(msg)
        }
    }
}
