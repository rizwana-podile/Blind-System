package org.sightguide

import android.app.Application
import org.sightguide.core.accessibility.haptics.HapticPatternManager
import org.sightguide.core.audio.earcon.EarconPlayer
import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.core.location.DefaultLocationClient
import org.sightguide.core.location.LocationClient
import org.sightguide.core.permissions.PermissionCoordinator
import org.sightguide.core.sensors.compass.CompassSensorManager
import org.sightguide.core.storage.database.SightGuideDatabase
import org.sightguide.core.storage.datastore.UserSettingsDataStore
import org.sightguide.feature.nearby.repository.NearbyPlacesRepository
import org.sightguide.feature.safety.dispatcher.EmergencyDispatcher

/**
 * Global application container holding shared services and persistence singletons.
 */
class SightGuideApplication : Application() {

    lateinit var ttsManager: TextToSpeechManager
        private set

    lateinit var hapticManager: HapticPatternManager
        private set

    lateinit var earconPlayer: EarconPlayer
        private set

    lateinit var locationClient: LocationClient
        private set

    lateinit var compassSensorManager: CompassSensorManager
        private set

    lateinit var database: SightGuideDatabase
        private set

    lateinit var userSettingsDataStore: UserSettingsDataStore
        private set

    lateinit var nearbyPlacesRepository: NearbyPlacesRepository
        private set

    lateinit var emergencyDispatcher: EmergencyDispatcher
        private set

    lateinit var permissionCoordinator: PermissionCoordinator
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Core Audio & Accessibility
        ttsManager = TextToSpeechManager(this)
        hapticManager = HapticPatternManager(this)
        earconPlayer = EarconPlayer()

        // 2. Hardware Sensors & Location
        locationClient = DefaultLocationClient(this)
        compassSensorManager = CompassSensorManager(this)

        // 3. Persistence & Storage
        database = SightGuideDatabase.getInstance(this)
        userSettingsDataStore = UserSettingsDataStore(this)

        // 4. Feature Repositories & Dispatchers
        nearbyPlacesRepository = NearbyPlacesRepository(database.offlineAmenityDao())
        emergencyDispatcher = EmergencyDispatcher(this, database.auditTrailDao())
        permissionCoordinator = PermissionCoordinator(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ttsManager.shutdown()
        earconPlayer.release()
        compassSensorManager.stopListening()
    }
}
