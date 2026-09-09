package org.sightguide.feature.voice.model

/**
 * Clean semantic representation of a user voice instruction.
 */
sealed interface VoiceIntent {
    data object WhereAmI : VoiceIntent
    data object GetCompassDirection : VoiceIntent
    data class StartNavigation(val destination: String) : VoiceIntent
    data object StopNavigation : VoiceIntent
    data object HowFar : VoiceIntent
    data object RepeatLast : VoiceIntent

    data object DescribeScene : VoiceIntent
    data class FindObject(val targetObject: String) : VoiceIntent

    data object ReadText : VoiceIntent
    data object StopReading : VoiceIntent
    data object PauseReading : VoiceIntent
    data object ResumeReading : VoiceIntent

    data class FindNearby(val category: String) : VoiceIntent

    data object TriggerSos : VoiceIntent
    data object CancelSos : VoiceIntent
    data object CallEmergencyContact : VoiceIntent
    data object ShareLocation : VoiceIntent
    data object StopSharingLocation : VoiceIntent

    data object OpenSettings : VoiceIntent
    data object Help : VoiceIntent
    data class Unknown(val rawTranscript: String) : VoiceIntent
}
