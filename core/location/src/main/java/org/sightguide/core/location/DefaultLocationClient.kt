package org.sightguide.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.common.result.AppResult
import org.sightguide.core.common.result.ErrorCode
import org.sightguide.core.location.filter.PedestrianKalmanFilter

/**
 * FusedLocationProviderClient implementation with Kalman smoothing.
 */
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) : LocationClient {

    private val kalmanFilter = PedestrianKalmanFilter()

    override fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMillis: Long): Flow<Coordinates> = callbackFlow {
        if (!isLocationServiceEnabled()) {
            close(IllegalStateException("Location services are disabled"))
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
            .setMinUpdateIntervalMillis(intervalMillis / 2)
            .setMinUpdateDistanceMeters(1.0f) // 1 meter sensitivity for pedestrian walking
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) {
                    val filtered = kalmanFilter.filter(
                        rawLat = loc.latitude,
                        rawLng = loc.longitude,
                        rawAccuracyMeters = loc.accuracy,
                        timeMillis = loc.time
                    )

                    val coords = Coordinates(
                        latitude = filtered.latitude,
                        longitude = filtered.longitude,
                        altitudeMeters = if (loc.hasAltitude()) loc.altitude else null,
                        accuracyMeters = filtered.accuracyMeters,
                        speedMps = if (loc.hasSpeed()) loc.speed else null,
                        bearingDegrees = if (loc.hasBearing()) loc.bearing else null,
                        timestampMillis = loc.time
                    )
                    trySend(coords)
                }
            }
        }

        client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(locationCallback)
            kalmanFilter.reset()
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppResult<Coordinates> {
        if (!isLocationServiceEnabled()) {
            return AppResult.Error(
                userFriendlyMessage = "Location service is currently disabled. Please enable GPS in device settings.",
                code = ErrorCode.HARDWARE_UNAVAILABLE
            )
        }

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location = client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                AppResult.Success(
                    Coordinates(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitudeMeters = if (location.hasAltitude()) location.altitude else null,
                        accuracyMeters = location.accuracy,
                        speedMps = if (location.hasSpeed()) location.speed else null,
                        bearingDegrees = if (location.hasBearing()) location.bearing else null,
                        timestampMillis = location.time
                    )
                )
            } else {
                AppResult.Error(
                    userFriendlyMessage = "Acquiring GPS location. Please ensure you have a clear view of the sky.",
                    code = ErrorCode.TIMEOUT
                )
            }
        } catch (e: Exception) {
            AppResult.Error(
                throwable = e,
                userFriendlyMessage = "Unable to determine current position: ${e.localizedMessage}",
                code = ErrorCode.UNKNOWN
            )
        }
    }
}
