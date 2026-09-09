package org.sightguide.core.location

import kotlinx.coroutines.flow.Flow
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.common.result.AppResult

/**
 * Clean architectural abstraction for location queries and continuous streams.
 */
interface LocationClient {

    /**
     * Continuous stream of smoothed coordinates for walking navigation.
     */
    fun getLocationUpdates(intervalMillis: Long = 3000L): Flow<Coordinates>

    /**
     * One-shot coordinate lookup (e.g. for "Where am I?" and SOS dispatch).
     */
    suspend fun getCurrentLocation(): AppResult<Coordinates>

    /**
     * Checks if the device location provider (GPS/Network) is enabled.
     */
    fun isLocationServiceEnabled(): Boolean
}
