package org.sightguide.core.accessibility.semantics

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics

/**
 * Convenience modifier to declare a composable as an accessible TalkBack heading.
 * Allows screen-reader users to navigate efficiently by jumping across headings.
 */
fun Modifier.accessibleHeading(): Modifier = this.semantics {
    heading()
}

/**
 * Marks a composable region as a live region for assertive TalkBack updates
 * (e.g., turn-by-turn alerts, countdowns, obstacle notifications).
 */
fun Modifier.assertiveLiveRegion(): Modifier = this.semantics {
    liveRegion = LiveRegionMode.Assertive
}

/**
 * Attaches a named custom accessibility action directly to a composable.
 */
fun Modifier.accessibleAction(
    label: String,
    action: () -> Boolean
): Modifier = this.semantics {
    customActions = listOf(
        CustomAccessibilityAction(label = label, action = action)
    )
}
