package org.sightguide.feature.camera.ui

import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.viewinterop.AndroidView
import org.sightguide.core.accessibility.semantics.accessibleHeading
import org.sightguide.core.designsystem.component.AccessibleButton
import org.sightguide.core.designsystem.theme.HighContrastDarkCritical
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.core.designsystem.theme.HighContrastDarkSuccess
import org.sightguide.feature.camera.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            text = "Camera Assistance",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.accessibleHeading()
        )

        // Camera Preview Area with High-Contrast Border
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .semantics {
                    contentDescription = "Camera viewfinder. Actively analyzing objects and obstacles in front of the lens."
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // High-visibility viewfinder indicator
                Text(
                    text = if (uiState.isSearching) "Searching for: ${uiState.searchTarget}" else "Camera Active",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Live Spoken Scene Description Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = "Scene description: ${uiState.lastSummary}"
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
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "  Surroundings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = uiState.lastSummary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Detected Objects Summary List
        if (uiState.detectedObstacles.isNotEmpty()) {
            Text(
                text = "Detected in View (${uiState.detectedObstacles.size})",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.accessibleHeading()
            )

            uiState.detectedObstacles.forEach { obstacle ->
                val borderColor = if (obstacle.isHazard) HighContrastDarkCritical else HighContrastDarkSuccess

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = obstacle.spokenDescription
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.5.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RemoveRedEye,
                            contentDescription = null,
                            tint = borderColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = obstacle.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${obstacle.confidence.name.replace('_', ' ')} • ${obstacle.spatialPosition.spokenPhrase}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Accessible Controls
        AccessibleButton(
            text = "Describe Scene",
            icon = Icons.Filled.Visibility,
            onClick = { viewModel.describeScene() },
            contentDescriptionText = "Describe scene. Double tap to speak a complete description of current obstacles."
        )

        AccessibleButton(
            text = if (uiState.isTorchEnabled) "Turn Off Flashlight" else "Turn On Flashlight",
            icon = if (uiState.isTorchEnabled) Icons.Filled.FlashOff else Icons.Filled.FlashOn,
            onClick = { viewModel.toggleTorch() },
            contentDescriptionText = "Toggle camera flashlight for low-light vision assistance."
        )

        if (uiState.isSearching) {
            AccessibleButton(
                text = "Stop Searching",
                isCritical = true,
                onClick = { viewModel.stopSearch() },
                contentDescriptionText = "Stop object search."
            )
        }
    }
}
