package org.sightguide.feature.nearby.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import org.sightguide.core.designsystem.theme.HighContrastDarkPrimary
import org.sightguide.core.designsystem.theme.HighContrastDarkSuccess
import org.sightguide.core.storage.entity.AmenityCategory
import org.sightguide.feature.nearby.model.NearbyPlaceItem
import org.sightguide.feature.nearby.viewmodel.NearbyViewModel

@Composable
fun NearbyScreen(
    viewModel: NearbyViewModel,
    onNavigateToPlace: (NearbyPlaceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val filterScrollState = rememberScrollState()

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
            text = "Nearby Amenities",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.accessibleHeading()
        )

        // Filter Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(filterScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip(
                label = "All Places",
                isSelected = uiState.selectedCategory == null,
                onClick = { viewModel.searchNearby(null) }
            )
            CategoryChip(
                label = "Pharmacies",
                isSelected = uiState.selectedCategory == AmenityCategory.PHARMACY,
                onClick = { viewModel.searchNearby(AmenityCategory.PHARMACY) }
            )
            CategoryChip(
                label = "Hospitals",
                isSelected = uiState.selectedCategory == AmenityCategory.HOSPITAL,
                onClick = { viewModel.searchNearby(AmenityCategory.HOSPITAL) }
            )
            CategoryChip(
                label = "Transit",
                isSelected = uiState.selectedCategory == AmenityCategory.TRANSIT,
                onClick = { viewModel.searchNearby(AmenityCategory.TRANSIT) }
            )
            CategoryChip(
                label = "Groceries",
                isSelected = uiState.selectedCategory == AmenityCategory.GROCERY,
                onClick = { viewModel.searchNearby(AmenityCategory.GROCERY) }
            )
        }

        // Summary Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = "Search status: ${uiState.statusMessage}"
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = uiState.statusMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Places List
        uiState.places.forEach { place ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "${place.spokenDescription}. Located at ${place.address}. Double tap Navigate to start walking guidance."
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${place.category.spokenLabel} • ${place.address}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${place.distanceMeters.toInt()} meters • ${place.clockPosition.spokenDescription}",
                        style = MaterialTheme.typography.titleMedium,
                        color = HighContrastDarkSuccess
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    AccessibleButton(
                        text = "Navigate Here",
                        icon = Icons.Filled.DirectionsWalk,
                        onClick = { viewModel.onPlaceClicked(place, onNavigateToPlace) },
                        contentDescriptionText = "Navigate to ${place.name}. Double tap to begin walking guidance."
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "$label filter. ${if (isSelected) "Selected" else "Not selected"}. Double tap to filter."
            },
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
