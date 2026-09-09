package org.sightguide.feature.safety.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.sightguide.core.accessibility.semantics.accessibleHeading
import org.sightguide.core.designsystem.component.AccessibleButton
import org.sightguide.core.designsystem.theme.HighContrastDarkCritical
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.core.designsystem.theme.HighContrastDarkSuccess
import org.sightguide.feature.safety.viewmodel.SafetyViewModel

@Composable
fun SafetyScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Safety & Emergency",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.accessibleHeading()
        )

        // Active SOS Countdown Modal Banner
        if (state.isSosCountingDown) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        liveRegion = LiveRegionMode.Assertive
                        contentDescription = "Emergency countdown: ${state.countdownSecondsRemaining} seconds remaining before dispatch. Double tap Cancel SOS to abort."
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HighContrastDarkCritical),
                border = BorderStroke(3.dp, HighContrastDarkPrimary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "EMERGENCY SOS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onError
                    )

                    Text(
                        text = "${state.countdownSecondsRemaining}",
                        style = MaterialTheme.typography.displayLarge,
                        color = HighContrastDarkPrimary
                    )

                    AccessibleButton(
                        text = "CANCEL SOS",
                        icon = Icons.Filled.Cancel,
                        onClick = { viewModel.cancelSos() },
                        contentDescriptionText = "Cancel SOS. Double tap immediately to abort emergency dispatch.",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Master SOS Trigger Button
            AccessibleButton(
                text = "TRIGGER EMERGENCY SOS",
                icon = Icons.Filled.Warning,
                isCritical = true,
                onClick = { viewModel.triggerSos() },
                contentDescriptionText = "Trigger Emergency SOS. Double tap to start 5-second countdown to alert contacts.",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Safety Status: ${state.statusNotice}"
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = state.statusNotice,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Primary Contact Call Tile
        if (state.primaryContact != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Primary contact: ${state.primaryContact!!.name}, relationship: ${state.primaryContact!!.relationship}. Double tap Call Primary Contact to dial."
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Primary Contact: ${state.primaryContact!!.name}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Relationship: ${state.primaryContact!!.relationship}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AccessibleButton(
                        text = "Call ${state.primaryContact!!.name}",
                        icon = Icons.Filled.Call,
                        onClick = { viewModel.callPrimaryContact() },
                        contentDescriptionText = "Call primary emergency contact ${state.primaryContact!!.name}."
                    )
                }
            }
        }

        // Consent-Based Location Sharing Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Location sharing status: ${if (state.isLocationSharingActive) "Active with trusted caregiver" else "Disabled"}. Double tap toggle button to change."
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, if (state.isLocationSharingActive) HighContrastDarkSuccess else MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ShareLocation,
                        contentDescription = null,
                        tint = if (state.isLocationSharingActive) HighContrastDarkSuccess else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "  Location Sharing with Caregiver",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (state.isLocationSharingActive) {
                        "Status: ACTIVELY SHARING with authorized caregiver. You can stop sharing at any time."
                    } else {
                        "Status: DISABLED. Your location is completely private and never shared."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AccessibleButton(
                    text = if (state.isLocationSharingActive) "Stop Sharing Location" else "Start Sharing Location",
                    onClick = { viewModel.toggleLocationSharing() },
                    isCritical = state.isLocationSharingActive,
                    contentDescriptionText = if (state.isLocationSharingActive) "Stop sharing location immediately." else "Start consent-based location sharing."
                )
            }
        }
    }
}
