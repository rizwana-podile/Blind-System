package org.sightguide.feature.dashboard.model

/**
 * UI State for the accessible dashboard and status indicators.
 */
data class DashboardUiState(
    val batteryPercent: Int = 100,
    val isBatteryCharging: Boolean = false,
    val gpsStatus: GpsStatus = GpsStatus.ACTIVE,
    val isNetworkAvailable: Boolean = true,
    val isLocationSharingActive: Boolean = false,
    val isVoiceListening: Boolean = false,
    val lastAnnouncedSpokenStatus: String = "SIGHTGUIDE ready. What can I help with?"
)

enum class GpsStatus(val label: String, val spokenDescription: String) {
    ACTIVE("GPS Active", "GPS accuracy good"),
    SEARCHING("GPS Searching", "Acquiring GPS location"),
    DISABLED("GPS Off", "Location services disabled in device settings")
}
