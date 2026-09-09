package org.sightguide.feature.safety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.location.LocationClient
import org.sightguide.core.storage.dao.EmergencyContactDao
import org.sightguide.core.storage.datastore.UserSettingsDataStore
import org.sightguide.core.storage.entity.EmergencyContactEntity
import org.sightguide.feature.safety.dispatcher.EmergencyDispatcher
import org.sightguide.feature.safety.model.SafetyState

/**
 * Safety ViewModel managing guarded SOS countdowns, emergency calls,
 * and revocable consent-based location sharing.
 */
class SafetyViewModel(
    private val contactDao: EmergencyContactDao,
    private val emergencyDispatcher: EmergencyDispatcher,
    private val locationClient: LocationClient,
    private val userSettingsDataStore: UserSettingsDataStore,
    private val ttsManager: TextToSpeechManager,
    private val hapticManager: HapticPatternManager
) : ViewModel() {

    private val _state = MutableStateFlow(SafetyState())
    val state: StateFlow<SafetyState> = _state.asStateFlow()

    private var countdownJob: Job? = null

    init {
        observeContactsAndSettings()
    }

    private fun observeContactsAndSettings() {
        viewModelScope.launch {
            contactDao.getAllContacts().collect { list ->
                val primary = list.firstOrNull { it.isPrimary } ?: list.firstOrNull()
                _state.update { it.copy(contacts = list, primaryContact = primary) }
            }
        }

        viewModelScope.launch {
            userSettingsDataStore.isLocationSharingActive.collect { active ->
                _state.update { it.copy(isLocationSharingActive = active) }
            }
        }
    }

    fun triggerSos() {
        countdownJob?.cancel()

        _state.update {
            it.copy(
                isSosCountingDown = true,
                countdownSecondsRemaining = 5,
                isSosDispatched = false,
                statusNotice = "SOS Countdown Active! Say 'Cancel SOS' or double-tap Cancel to abort."
            )
        }

        ttsManager.speak("SOS activated. Dispatching emergency alert in 5 seconds. Double tap Cancel to abort.")

        countdownJob = viewModelScope.launch {
            for (sec in 5 downTo 1) {
                _state.update { it.copy(countdownSecondsRemaining = sec) }
                hapticManager.sosCountdownBeat()
                if (sec < 5) {
                    ttsManager.speak("$sec", interruptIfSpeaking = true)
                }
                delay(1000L)
            }

            executeSosDispatch()
        }
    }

    fun cancelSos() {
        countdownJob?.cancel()
        countdownJob = null
        hapticManager.confirm()

        _state.update {
            it.copy(
                isSosCountingDown = false,
                statusNotice = "SOS cancelled. You are safe."
            )
        }

        viewModelScope.launch {
            emergencyDispatcher.recordSosCancelled()
        }

        ttsManager.speak("SOS cancelled. Emergency alert was not sent.")
    }

    private suspend fun executeSosDispatch() {
        _state.update {
            it.copy(
                isSosCountingDown = false,
                isSosDispatched = true,
                statusNotice = "Emergency SOS has been dispatched to your trusted contacts."
            )
        }

        val location = locationClient.getCurrentLocation().getOrNull()
        val contacts = _state.value.contacts

        emergencyDispatcher.dispatchSosSms(contacts, location)

        val primary = _state.value.primaryContact
        if (primary != null && primary.canReceiveCall) {
            ttsManager.speak("Emergency alert sent. Calling primary contact ${primary.name}.")
            emergencyDispatcher.dialContact(primary)
        } else {
            ttsManager.speak("Emergency alert sent to trusted contacts.")
        }
    }

    fun callPrimaryContact() {
        val primary = _state.value.primaryContact
        if (primary != null) {
            hapticManager.confirm()
            ttsManager.speak("Calling ${primary.name}")
            emergencyDispatcher.dialContact(primary)
        } else {
            ttsManager.speak("No primary contact configured. Please add an emergency contact.")
        }
    }

    fun toggleLocationSharing() {
        hapticManager.confirm()
        val next = !_state.value.isLocationSharingActive
        viewModelScope.launch {
            userSettingsDataStore.setLocationSharingActive(next)
            val msg = if (next) {
                "Consent-based location sharing enabled with your trusted caregiver."
            } else {
                "Location sharing has been stopped."
            }
            ttsManager.speak(msg)
        }
    }

    fun addContact(name: String, phone: String, relationship: String, isPrimary: Boolean = false) {
        viewModelScope.launch {
            val contact = EmergencyContactEntity(
                name = name,
                phoneNumber = phone,
                relationship = relationship,
                isPrimary = isPrimary
            )
            val id = contactDao.insertContact(contact)
            if (isPrimary) {
                contactDao.clearOtherPrimaryContacts(id)
            }
            ttsManager.speak("Saved emergency contact $name")
        }
    }
}
