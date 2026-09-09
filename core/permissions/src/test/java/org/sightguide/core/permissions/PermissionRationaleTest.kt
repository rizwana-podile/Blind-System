package org.sightguide.core.permissions

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionRationaleTest {

    @Test
    fun allPermissions_haveNonEmptySpokenRationalesAndFallbacks() {
        for (perm in SightGuidePermission.entries) {
            assertNotNull(perm.featureName)
            assertFalse(perm.featureName.isBlank())

            assertNotNull(perm.spokenRationale)
            assertFalse(perm.spokenRationale.isBlank())

            assertNotNull(perm.spokenDenialFallback)
            assertFalse(perm.spokenDenialFallback.isBlank())
        }
    }

    @Test
    fun locationPermission_mentionsSafetyAndGuidance() {
        val rationale = SightGuidePermission.LOCATION.spokenRationale
        assertTrue(rationale.contains("walking") || rationale.contains("guidance"))
    }
}
