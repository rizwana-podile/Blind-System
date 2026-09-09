package org.sightguide.feature.caregiver.ui

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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShareLocation
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.sightguide.core.accessibility.semantics.accessibleHeading
import org.sightguide.core.designsystem.component.AccessibleButton
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.core.designsystem.theme.HighContrastDarkSuccess
import org.sightguide.feature.caregiver.viewmodel.CaregiverViewModel

@Composable
fun CaregiverScreen(
    viewModel: CaregiverViewModel,
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
            text = "Caregiver Coordination",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.accessibleHeading()
        )

        // Privacy Guarantee Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Privacy policy: SIGHTGUIDE never shares your location without your explicit knowledge and consent. You hold complete control to revoke sharing anytime."
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, HighContrastDarkSuccess)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = HighContrastDarkSuccess,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Zero Covert Tracking: Location sharing is entirely consent-based and immediately revocable.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // Active Sharing Status & Toggle Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Location sharing status: ${if (state.isSharingActive) "Active" else "Disabled"}."
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, if (state.isSharingActive) HighContrastDarkSuccess else MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ShareLocation,
                        contentDescription = null,
                        tint = if (state.isSharingActive) HighContrastDarkSuccess else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "  Sharing Status",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (state.isSharingActive) {
                        "Status: ACTIVELY SHARING with authorized caregiver."
                    } else {
                        "Status: STOPPED. No location data is being relayed."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AccessibleButton(
                    text = if (state.isSharingActive) "Stop Sharing Now" else "Enable Location Sharing",
                    onClick = { viewModel.toggleLocationSharing() },
                    isCritical = state.isSharingActive,
                    contentDescriptionText = if (state.isSharingActive) "Stop sharing location immediately." else "Enable consent-based location sharing."
                )
            }
        }

        // Secure Pairing Passkey Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = if (state.activePairingCode != null) {
                        "Active pairing passkey: ${state.activePairingCode}. Share this with caregiver."
                    } else {
                        "No active pairing code. Double tap Generate Pairing Code to create one."
                    }
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "  Caregiver Pairing Code",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (state.activePairingCode != null) {
                    Text(
                        text = state.activePairingCode!!,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Temporary code valid for in-person caregiver connection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AccessibleButton(
                    text = "Generate Pairing Code",
                    icon = Icons.Filled.Key,
                    onClick = { viewModel.generateNewPairingCode() },
                    contentDescriptionText = "Generate new temporary pairing code for caregiver connection."
                )
            }
        }

        // Privacy Audit Log History
        if (state.auditLogs.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "  Privacy Audit Trail",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.accessibleHeading()
                )
            }

            state.auditLogs.take(5).forEach { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Event: ${log.eventType}. Details: ${log.eventDetails}."
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = log.eventType,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = log.eventDetails,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
