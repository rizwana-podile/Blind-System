package org.sightguide.feature.reader.ui

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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.viewinterop.AndroidView
import org.sightguide.core.accessibility.semantics.accessibleHeading
import org.sightguide.core.designsystem.component.AccessibleButton
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.core.designsystem.theme.HighContrastDarkSuccess
import org.sightguide.feature.reader.viewmodel.ReaderViewModel

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
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
            text = "Text & Document Reader",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.accessibleHeading()
        )

        // Viewfinder Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .semantics {
                    contentDescription = "Document scanner viewfinder. Point camera at text, signs, or packaging."
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

                Text(
                    text = if (uiState.document != null) uiState.document!!.documentType.spokenName else "Scanning Text...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Status / Document Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = "Reader status: ${uiState.statusMessage}"
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
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "  Reading Status",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = uiState.statusMessage,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (uiState.document?.keyHighlight != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Key Highlight: ${uiState.document!!.keyHighlight}",
                        style = MaterialTheme.typography.titleMedium,
                        color = HighContrastDarkSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Large Accessible Action Buttons
        AccessibleButton(
            text = "Read Now",
            icon = Icons.Filled.PlayArrow,
            onClick = { viewModel.readNow() },
            contentDescriptionText = "Read now. Double tap to scan text and read aloud."
        )

        if (uiState.isReading) {
            AccessibleButton(
                text = "Pause Reading",
                icon = Icons.Filled.Pause,
                onClick = { viewModel.pauseReading() },
                contentDescriptionText = "Pause reading."
            )
        } else if (uiState.isPaused) {
            AccessibleButton(
                text = "Resume Reading",
                icon = Icons.Filled.PlayArrow,
                onClick = { viewModel.resumeReading() },
                contentDescriptionText = "Resume reading."
            )
        }

        AccessibleButton(
            text = "Repeat",
            icon = Icons.Filled.Replay,
            onClick = { viewModel.repeat() },
            contentDescriptionText = "Repeat last sentence."
        )

        if (uiState.isReading || uiState.isPaused) {
            AccessibleButton(
                text = "Stop Reading",
                icon = Icons.Filled.Stop,
                isCritical = true,
                onClick = { viewModel.stopReading() },
                contentDescriptionText = "Stop reading."
            )
        }
    }
}
