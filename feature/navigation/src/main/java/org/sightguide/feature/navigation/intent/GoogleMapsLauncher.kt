package org.sightguide.feature.navigation.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.sightguide.core.common.model.Coordinates

/**
 * Utility to launch external Google Maps walking navigation via standard Android Intents.
 * Avoids hardcoded API keys and allows seamless fallback to the user's preferred mapping app.
 */
object GoogleMapsLauncher {

    fun launchWalkingNavigation(context: Context, destination: Coordinates, label: String = "Destination"): Boolean {
        // Preferred: google.navigation intent for turn-by-turn walking mode
        val navUri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, navUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(mapIntent)
            true
        } catch (_: Exception) {
            // Fallback: generic geo intent or web browser fallback
            launchGeoFallback(context, destination, label)
        }
    }

    private fun launchGeoFallback(context: Context, destination: Coordinates, label: String): Boolean {
        val geoUri = Uri.parse("geo:${destination.latitude},${destination.longitude}?q=${destination.latitude},${destination.longitude}($label)")
        val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(fallbackIntent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
