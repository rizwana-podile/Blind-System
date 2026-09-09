package org.sightguide.feature.voice.router

import org.sightguide.core.audio.tts.TextToSpeechManager
import org.sightguide.feature.voice.model.VoiceIntent

/**
 * Dispatches recognized VoiceIntents to application feature controllers
 * and coordinates spoken responses.
 */
class VoiceCommandRouter(
    private val ttsManager: TextToSpeechManager,
    private val actionHandler: ActionHandler
) {

    interface ActionHandler {
        fun onWhereAmIRequested()
        fun onCompassHeadingRequested()
        fun onNavigationRequested(destination: String)
        fun onStopNavigationRequested()
        fun onHowFarRequested()
        fun onDescribeSceneRequested()
        fun onFindObjectRequested(target: String)
        fun onReadTextRequested()
        fun onStopReadingRequested()
        fun onPauseReadingRequested()
        fun onResumeReadingRequested()
        fun onFindNearbyRequested(category: String)
        fun onTriggerSosRequested()
        fun onCancelSosRequested()
        fun onCallEmergencyRequested()
        fun onShareLocationRequested()
        fun onStopSharingLocationRequested()
        fun onOpenSettingsRequested()
    }

    fun route(intent: VoiceIntent) {
        when (intent) {
            is VoiceIntent.RepeatLast -> {
                ttsManager.repeatLast()
            }
            is VoiceIntent.WhereAmI -> {
                actionHandler.onWhereAmIRequested()
            }
            is VoiceIntent.GetCompassDirection -> {
                actionHandler.onCompassHeadingRequested()
            }
            is VoiceIntent.StartNavigation -> {
                actionHandler.onNavigationRequested(intent.destination)
            }
            is VoiceIntent.StopNavigation -> {
                actionHandler.onStopNavigationRequested()
            }
            is VoiceIntent.HowFar -> {
                actionHandler.onHowFarRequested()
            }
            is VoiceIntent.DescribeScene -> {
                actionHandler.onDescribeSceneRequested()
            }
            is VoiceIntent.FindObject -> {
                actionHandler.onFindObjectRequested(intent.targetObject)
            }
            is VoiceIntent.ReadText -> {
                actionHandler.onReadTextRequested()
            }
            is VoiceIntent.StopReading -> {
                actionHandler.onStopReadingRequested()
            }
            is VoiceIntent.PauseReading -> {
                actionHandler.onPauseReadingRequested()
            }
            is VoiceIntent.ResumeReading -> {
                actionHandler.onResumeReadingRequested()
            }
            is VoiceIntent.FindNearby -> {
                actionHandler.onFindNearbyRequested(intent.category)
            }
            is VoiceIntent.TriggerSos -> {
                actionHandler.onTriggerSosRequested()
            }
            is VoiceIntent.CancelSos -> {
                actionHandler.onCancelSosRequested()
            }
            is VoiceIntent.CallEmergencyContact -> {
                actionHandler.onCallEmergencyRequested()
            }
            is VoiceIntent.ShareLocation -> {
                actionHandler.onShareLocationRequested()
            }
            is VoiceIntent.StopSharingLocation -> {
                actionHandler.onStopSharingLocationRequested()
            }
            is VoiceIntent.OpenSettings -> {
                actionHandler.onOpenSettingsRequested()
            }
            is VoiceIntent.Help -> {
                ttsManager.speak(
                    "You can say: Where am I, What's my direction, Navigate to, Describe scene, " +
                            "Find my bottle, Read this, What's around me, Call emergency contact, or Settings."
                )
            }
            is VoiceIntent.Unknown -> {
                ttsManager.speak("I didn't understand. Please try saying: Help, Navigate, Read this, or Where am I.")
            }
        }
    }
}
