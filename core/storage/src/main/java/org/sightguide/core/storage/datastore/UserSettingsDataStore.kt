package org.sightguide.core.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * DataStore-backed storage for user accessibility settings, speech preferences,
 * and emergency configuration.
 */
class UserSettingsDataStore(private val context: Context) {

    companion object {
        val KEY_SPEECH_RATE = floatPreferencesKey("speech_rate")
        val KEY_SPEECH_PITCH = floatPreferencesKey("speech_pitch")
        val KEY_HIGH_CONTRAST_THEME = booleanPreferencesKey("high_contrast_theme")
        val KEY_HAPTIC_FEEDBACK_LEVEL = intPreferencesKey("haptic_feedback_level") // 0=Off, 1=Gentle, 2=Normal, 3=Strong
        val KEY_AUDIO_CHIMES_ENABLED = booleanPreferencesKey("audio_chimes_enabled")
        val KEY_SOS_COUNTDOWN_SECONDS = intPreferencesKey("sos_countdown_seconds")
        val KEY_CONSENT_LOCATION_SHARING = booleanPreferencesKey("consent_location_sharing")
        val KEY_CAREGIVER_PAIRING_ID = stringPreferencesKey("caregiver_pairing_id")
    }

    val speechRate: Flow<Float> = context.userDataStore.data.map { prefs ->
        prefs[KEY_SPEECH_RATE] ?: 1.0f
    }

    val speechPitch: Flow<Float> = context.userDataStore.data.map { prefs ->
        prefs[KEY_SPEECH_PITCH] ?: 1.0f
    }

    val isHighContrastTheme: Flow<Boolean> = context.userDataStore.data.map { prefs ->
        prefs[KEY_HIGH_CONTRAST_THEME] ?: true // Default high-contrast on
    }

    val hapticLevel: Flow<Int> = context.userDataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_FEEDBACK_LEVEL] ?: 2 // Default normal
    }

    val isAudioChimesEnabled: Flow<Boolean> = context.userDataStore.data.map { prefs ->
        prefs[KEY_AUDIO_CHIMES_ENABLED] ?: true
    }

    val sosCountdownSeconds: Flow<Int> = context.userDataStore.data.map { prefs ->
        prefs[KEY_SOS_COUNTDOWN_SECONDS] ?: 5
    }

    val isLocationSharingActive: Flow<Boolean> = context.userDataStore.data.map { prefs ->
        prefs[KEY_CONSENT_LOCATION_SHARING] ?: false
    }

    suspend fun setSpeechRate(rate: Float) {
        context.userDataStore.edit { it[KEY_SPEECH_RATE] = rate }
    }

    suspend fun setSpeechPitch(pitch: Float) {
        context.userDataStore.edit { it[KEY_SPEECH_PITCH] = pitch }
    }

    suspend fun setHighContrastTheme(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_HIGH_CONTRAST_THEME] = enabled }
    }

    suspend fun setHapticLevel(level: Int) {
        context.userDataStore.edit { it[KEY_HAPTIC_FEEDBACK_LEVEL] = level }
    }

    suspend fun setAudioChimesEnabled(enabled: Boolean) {
        context.userDataStore.edit { it[KEY_AUDIO_CHIMES_ENABLED] = enabled }
    }

    suspend fun setSosCountdownSeconds(seconds: Int) {
        context.userDataStore.edit { it[KEY_SOS_COUNTDOWN_SECONDS] = seconds.coerceIn(3, 10) }
    }

    suspend fun setLocationSharingActive(active: Boolean) {
        context.userDataStore.edit { it[KEY_CONSENT_LOCATION_SHARING] = active }
    }

    suspend fun setCaregiverPairingId(pairingId: String?) {
        context.userDataStore.edit {
            if (pairingId == null) {
                it.remove(KEY_CAREGIVER_PAIRING_ID)
            } else {
                it[KEY_CAREGIVER_PAIRING_ID] = pairingId
            }
        }
    }
}
