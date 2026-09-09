package org.sightguide.feature.navigation.ui

import android.content.Context
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
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Replay
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.sightguide.core.accessibility.semantics.accessibleHeading
import org.sightguide.core.designsystem.component.AccessibleButton
import org.sightguide.core.designsystem.theme.HighContrastDarkCritical
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.feature.navigation.intent.GoogleMapsLauncher
import org.sightguide.feature.navigation.viewmodel.NavigationViewModel

@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        Text(
            text = "Walking Navigation",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.accessibleHeading()
        )

        // Safety Advisory Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Safety notice: Assistive navigation only. Does not guarantee obstacle avoidance. Always use mobility cane or guide dog."
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.5.dp, HighContrastDarkPrimary)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = HighContrastDarkPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Assistive navigation only. Always use mobility cane or guide dog for physical obstacles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // Compass Heading Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Compass: Facing ${state.compassDirection.spokenName}, ${state.currentHeadingDegrees.toInt()} degrees."
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Explore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "  Current Heading",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${state.compassDirection.spokenName} (${state.currentHeadingDegrees.toInt()}°)",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Active Turn-by-Turn Instruction Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = "Guidance instruction: ${state.nextInstruction}"
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Instruction",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = state.nextInstruction,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Accessible Action Buttons
        AccessibleButton(
            text = "Where Am I?",
            icon = Icons.Filled.MyLocation,
            onClick = { viewModel.whereAmI() },
            contentDescriptionText = "Where am I? Double tap to hear your current coordinates and facing direction."
        )

        AccessibleButton(
            text = "What's My Direction?",
            icon = Icons.Filled.CompassCalibration,
            onClick = { viewModel.announceDirection() },
            contentDescriptionText = "What is my direction? Double tap to hear compass heading."
        )

        AccessibleButton(
            text = "Repeat Instruction",
            icon = Icons.Filled.Replay,
            onClick = { viewModel.repeatLastInstruction() },
            contentDescriptionText = "Repeat instruction. Double tap to hear the last navigation instruction again."
        )

        if (state.isNavigating && state.destinationCoords != null) {
            AccessibleButton(
                text = "Open in Google Maps",
                icon = Icons.Filled.Map,
                onClick = {
                    GoogleMapsLauncher.launchWalkingNavigation(
                        context = context,
                        destination = state.destinationCoords!!,
                        label = state.destinationName ?: "Destination"
                    )
                },
                contentDescriptionText = "Open in Google Maps. Double tap to launch external Maps walking directions."
            )

            AccessibleButton(
                text = "Stop Navigation",
                isCritical = true,
                onClick = { viewModel.stopNavigation() },
                contentDescriptionText = "Stop navigation. Double tap to end walking route."
            )
        }
    }
}
