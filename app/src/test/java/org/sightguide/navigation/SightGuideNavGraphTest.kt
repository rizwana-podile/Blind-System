package org.sightguide.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class SightGuideNavGraphTest {

    @Test
    fun screenRoutes_haveExpectedIdentifiers() {
        assertEquals("dashboard", Screen.Dashboard.route)
        assertEquals("navigation", Screen.Navigation.route)
        assertEquals("camera", Screen.Camera.route)
        assertEquals("reader", Screen.Reader.route)
        assertEquals("nearby", Screen.Nearby.route)
        assertEquals("safety", Screen.Safety.route)
        assertEquals("caregiver", Screen.Caregiver.route)
        assertEquals("settings", Screen.Settings.route)
    }
}
