package org.sightguide.core.accessibility.manager

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager

/**
 * Monitors the system accessibility state to dynamically adjust spoken guidance
 * and prevent speech collision with TalkBack.
 */
class AccessibilityServiceDetector(context: Context) {

    private val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

    /**
     * True if a screen reader like TalkBack or Android Accessibility Suite is currently active.
     */
    val isScreenReaderActive: Boolean
        get() {
            val am = accessibilityManager ?: return false
            if (!am.isEnabled) return false

            val runningServices = am.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_SPOKEN
            )
            return runningServices.isNotEmpty() || am.isTouchExplorationEnabled
        }

    /**
     * True if Touch Exploration (TalkBack gesture navigation) is currently enabled.
     */
    val isTouchExplorationActive: Boolean
        get() = accessibilityManager?.isTouchExplorationEnabled == true
}
