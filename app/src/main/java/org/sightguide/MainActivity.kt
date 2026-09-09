package org.sightguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.sightguide.core.designsystem.theme.SightGuideTheme
import org.sightguide.feature.voice.listener.VoiceInputListener
import org.sightguide.feature.voice.parser.NaturalLanguageIntentParser
import org.sightguide.feature.voice.router.VoiceCommandRouter
import org.sightguide.navigation.Screen
import org.sightguide.navigation.SightGuideNavGraph

/**
 * Main Activity of SIGHTGUIDE.
 * Acts as the top-level host for Compose navigation, edge-to-edge rendering,
 * and global voice assistant routing.
 */
class MainActivity : ComponentActivity(), VoiceCommandRouter.ActionHandler {

    private lateinit var app: SightGuideApplication
    private lateinit var voiceInputListener: VoiceInputListener
    private lateinit var voiceCommandRouter: VoiceCommandRouter
    private var activeNavController: androidx.navigation.NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        app = application as SightGuideApplication
        voiceInputListener = VoiceInputListener(this, app.earconPlayer)
        voiceCommandRouter = VoiceCommandRouter(app.ttsManager, this)

        // Observe voice recognizer output
        lifecycleScope.launch {
            voiceInputListener.recognizedTranscript.collect { transcript ->
                if (!transcript.isNullOrBlank()) {
                    val intent = NaturalLanguageIntentParser.parse(transcript)
                    voiceCommandRouter.route(intent)
                }
            }
        }

        setContent {
            val isDarkTheme by app.userSettingsDataStore.isHighContrastTheme.collectAsState(initial = true)
            val navController = rememberNavController()
            activeNavController = navController

            SightGuideTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SightGuideNavGraph(
                        app = app,
                        navController = navController,
                        onOpenVoiceTrigger = { voiceInputListener.startListening() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Voice Command Handlers
    override fun onWhereAmIRequested() {
        lifecycleScope.launch {
            val result = app.locationClient.getCurrentLocation()
            result.onSuccess { coords ->
                val dir = app.compassSensorManager.compassDirection.value.spokenName
                app.ttsManager.speak("You are near coordinates ${String.format("%.4f", coords.latitude)}, ${String.format("%.4f", coords.longitude)}, facing $dir.")
            }.onError { err ->
                app.ttsManager.speak(err.userFriendlyMessage)
            }
        }
    }

    override fun onCompassHeadingRequested() {
        val dir = app.compassSensorManager.compassDirection.value.spokenName
        val heading = app.compassSensorManager.headingDegrees.value.toInt()
        app.ttsManager.speak("Facing $dir, $heading degrees")
    }

    override fun onNavigationRequested(destination: String) {
        activeNavController?.navigate(Screen.Navigation.route)
        if (destination.isNotBlank()) {
            app.ttsManager.speak("Opening navigation. Searching for destination: $destination.")
        }
    }

    override fun onStopNavigationRequested() {
        activeNavController?.navigate(Screen.Dashboard.route)
        app.ttsManager.speak("Navigation cancelled. Back at main dashboard.")
    }

    override fun onHowFarRequested() {
        app.ttsManager.speak("Please open navigation to view remaining distance.")
    }

    override fun onDescribeSceneRequested() {
        activeNavController?.navigate(Screen.Camera.route)
        app.ttsManager.speak("Opening camera obstacle detector.")
    }

    override fun onFindObjectRequested(target: String) {
        activeNavController?.navigate(Screen.Camera.route)
        app.ttsManager.speak("Opening camera. Searching for $target.")
    }

    override fun onReadTextRequested() {
        activeNavController?.navigate(Screen.Reader.route)
        app.ttsManager.speak("Opening text and document reader.")
    }

    override fun onStopReadingRequested() {
        app.ttsManager.stop()
        app.ttsManager.speak("Reading stopped.")
    }

    override fun onPauseReadingRequested() {
        app.ttsManager.stop()
        app.ttsManager.speak("Reading paused.")
    }

    override fun onResumeReadingRequested() {
        app.ttsManager.speak("Resuming reading.")
    }

    override fun onFindNearbyRequested(category: String) {
        activeNavController?.navigate(Screen.Nearby.route)
        app.ttsManager.speak("Opening nearby amenities.")
    }

    override fun onTriggerSosRequested() {
        activeNavController?.navigate(Screen.Safety.route)
        app.ttsManager.speak("Opening Emergency SOS dashboard.")
    }

    override fun onCancelSosRequested() {
        app.ttsManager.speak("SOS cancelled.")
    }

    override fun onCallEmergencyRequested() {
        activeNavController?.navigate(Screen.Safety.route)
        app.ttsManager.speak("Opening emergency contact dialer.")
    }

    override fun onShareLocationRequested() {
        activeNavController?.navigate(Screen.Caregiver.route)
        app.ttsManager.speak("Opening caregiver location sharing.")
    }

    override fun onStopSharingLocationRequested() {
        lifecycleScope.launch {
            app.userSettingsDataStore.setLocationSharingActive(false)
            app.ttsManager.speak("Location sharing has been stopped.")
        }
    }

    override fun onOpenSettingsRequested() {
        activeNavController?.navigate(Screen.Settings.route)
        app.ttsManager.speak("Opening accessibility settings.")
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceInputListener.stopListening()
    }
}
