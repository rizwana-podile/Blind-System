package org.sightguide.core.permissions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.sightguide.core.designsystem.component.AccessibleButton

/**
 * High-contrast, accessible permission rationale dialog.
 * Reads the explanation clearly via TalkBack and provides large, unambiguous touch targets.
 */
@Composable
fun AccessiblePermissionDialog(
    permission: SightGuidePermission,
    onGrantRequested: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Permission: ${permission.featureName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = permission.spokenRationale,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                AccessibleButton(
                    text = "Allow Access",
                    onClick = onGrantRequested,
                    contentDescriptionText = "Allow access to ${permission.featureName}. Double tap to proceed to system permission prompt."
                )

                AccessibleButton(
                    text = "Skip for Now",
                    onClick = onDismiss,
                    contentDescriptionText = "Skip ${permission.featureName} permission for now. Double tap to decline."
                )
            }
        }
    }
}
