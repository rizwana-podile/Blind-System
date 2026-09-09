package org.sightguide.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.sightguide.core.accessibility.semantics.accessibleHeading
import org.sightguide.core.designsystem.component.AccessibleButton
import org.sightguide.core.designsystem.component.AccessibleTile
import org.sightguide.core.designsystem.component.StatusBadge
import org.sightguide.core.designsystem.theme.HighContrastDarkCritical
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.core.designsystem.theme.HighContrastDarkSuccess
import org.sightguide.feature.dashboard.model.GpsStatus
import org.sightguide.feature.dashboard.viewmodel.DashboardViewModel

/**
 * Primary SIGHTGUIDE Accessible Dashboard.
 * Engineered for non-visual and low-vision operation with large tactile touch targets,
 * high-contrast styling, and complete TalkBack semantics.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateClick: () -> Unit,
    onCameraClick: () -> Unit,
    onReadClick: () -> Unit,
    onNearbyClick: () -> Unit,
    onSafetyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.onDashboardOpened()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Text(
            text = "SIGHTGUIDE",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .accessibleHeading()
                .padding(vertical = 4.dp)
        )

        // System Health & Vitals Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val gpsColor = when (uiState.gpsStatus) {
                GpsStatus.ACTIVE -> HighContrastDarkSuccess
                GpsStatus.SEARCHING -> HighContrastDarkPrimary
                GpsStatus.DISABLED -> HighContrastDarkCritical
            }

            StatusBadge(
                label = uiState.gpsStatus.label,
                icon = Icons.Filled.GpsFixed,
                statusColor = gpsColor,
                accessibilityDescription = uiState.gpsStatus.spokenDescription,
                modifier = Modifier.clickable {
                    viewModel.onStatusBadgeClicked(uiState.gpsStatus.spokenDescription)
                }
            )

            StatusBadge(
                label = "${uiState.batteryPercent}%",
                icon = Icons.Filled.BatteryChargingFull,
                statusColor = HighContrastDarkSuccess,
                accessibilityDescription = "Battery level ${uiState.batteryPercent} percent",
                modifier = Modifier.clickable {
                    viewModel.onStatusBadgeClicked("Battery level ${uiState.batteryPercent} percent")
                }
            )

            StatusBadge(
                label = if (uiState.isNetworkAvailable) "Online" else "Offline",
                icon = Icons.Filled.Wifi,
                statusColor = if (uiState.isNetworkAvailable) HighContrastDarkSuccess else HighContrastDarkPrimary,
                accessibilityDescription = if (uiState.isNetworkAvailable) "Internet connected" else "Offline mode active. All core features work locally.",
                modifier = Modifier.clickable {
                    viewModel.onStatusBadgeClicked(if (uiState.isNetworkAvailable) "Connected to internet" else "Offline mode active")
                }
            )
        }

        // Voice Assistant Master Trigger
        AccessibleButton(
            text = "Voice Assistant",
            icon = Icons.Filled.Mic,
            onClick = { viewModel.onVoiceTriggerClicked() },
            contentDescriptionText = "Voice Assistant. Double tap to speak your command.",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Accessible Feature Action Grid
        AccessibleTile(
            title = "Navigate",
            subtitle = "Compass heading, pedestrian guidance, where am I",
            icon = Icons.Filled.Explore,
            accentColor = HighContrastDarkPrimary,
            onClick = {
                viewModel.onFeatureTileClicked("Navigation", "Opening walking directions and compass")
                onNavigateClick()
            }
        )

        AccessibleTile(
            title = "Camera",
            subtitle = "Obstacle detection, scene description, find items",
            icon = Icons.Filled.CameraAlt,
            accentColor = HighContrastDarkPrimary,
            onClick = {
                viewModel.onFeatureTileClicked("Camera", "Opening camera obstacle detector")
                onCameraClick()
            }
        )

        AccessibleTile(
            title = "Read",
            subtitle = "Read documents, packaging labels, signs, menus aloud",
            icon = Icons.Filled.MenuBook,
            accentColor = HighContrastDarkPrimary,
            onClick = {
                viewModel.onFeatureTileClicked("Reader", "Opening document and text reader")
                onReadClick()
            }
        )

        AccessibleTile(
            title = "Nearby",
            subtitle = "Pharmacies, hospitals, bus stops, ATMs, groceries",
            icon = Icons.Filled.Place,
            accentColor = HighContrastDarkPrimary,
            onClick = {
                viewModel.onFeatureTileClicked("Nearby", "Searching nearby essential places")
                onNearbyClick()
            }
        )

        AccessibleTile(
            title = "Safety & SOS",
            subtitle = "Emergency contacts, SOS countdown, location sharing",
            icon = Icons.Filled.Security,
            accentColor = HighContrastDarkCritical,
            isProminent = true,
            onClick = {
                viewModel.onFeatureTileClicked("Safety", "Opening emergency safety dashboard")
                onSafetyClick()
            }
        )

        AccessibleTile(
            title = "Settings",
            subtitle = "Speech speed, pitch, contrast, tactile feedback",
            icon = Icons.Filled.Settings,
            accentColor = HighContrastDarkPrimary,
            onClick = {
                viewModel.onFeatureTileClicked("Settings", "Opening accessibility settings")
                onSettingsClick()
            }
        )
    }
}
