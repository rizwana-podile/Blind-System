package org.sightguide.core.permissions

import android.Manifest
import android.os.Build

/**
 * Enumeration of hardware permissions required by SIGHTGUIDE with
 * conversational spoken rationales designed for blind and low-vision users.
 */
enum class SightGuidePermission(
    val androidPermission: String,
    val featureName: String,
    val spokenRationale: String,
    val spokenDenialFallback: String
) {
    LOCATION(
        androidPermission = Manifest.permission.ACCESS_FINE_LOCATION,
        featureName = "Location Guidance",
        spokenRationale = "SIGHTGUIDE requires your location to provide walking turn-by-turn guidance, compass directions, and emergency SOS coordinates.",
        spokenDenialFallback = "Location permission is not granted. Walking navigation and nearby place discovery are unavailable. You can still use the text reader and voice assistant."
    ),
    CAMERA(
        androidPermission = Manifest.permission.CAMERA,
        featureName = "Camera Vision",
        spokenRationale = "Camera access allows SIGHTGUIDE to detect obstacles in front of you and read signs, documents, and product labels aloud.",
        spokenDenialFallback = "Camera permission is denied. Obstacle detection and document reading are unavailable."
    ),
    MICROPHONE(
        androidPermission = Manifest.permission.RECORD_AUDIO,
        featureName = "Voice Assistant",
        spokenRationale = "Microphone access allows you to speak voice commands like 'Where am I?' and 'Read this sign'.",
        spokenDenialFallback = "Microphone access is denied. You can interact with SIGHTGUIDE using the accessible large-touch dashboard tiles."
    ),
    NOTIFICATIONS(
        androidPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        },
        featureName = "Guidance Notifications",
        spokenRationale = "Notifications allow SIGHTGUIDE to provide continuous spoken guidance while your phone is locked in your pocket.",
        spokenDenialFallback = "Notifications are disabled. Spoken turn guidance will pause when the screen turns off."
    );

    companion object {
        fun fromAndroidPermission(permission: String): SightGuidePermission? {
            return entries.find { it.androidPermission == permission }
        }
    }
}
