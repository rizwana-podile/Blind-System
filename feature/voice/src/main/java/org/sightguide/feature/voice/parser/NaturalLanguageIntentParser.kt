package org.sightguide.feature.voice.parser

import org.sightguide.feature.voice.model.VoiceIntent
import java.util.Locale

/**
 * Natural language intent parser for assistive voice commands.
 * Runs completely on-device without internet access, using tokenization
 * and semantic keyword matching.
 */
object NaturalLanguageIntentParser {

    fun parse(rawTranscript: String): VoiceIntent {
        val normalized = rawTranscript.trim().lowercase(Locale.ROOT)
            .replace(Regex("""[?.!,]"""), "")

        if (normalized.isBlank()) return VoiceIntent.Unknown("")

        return when {
            // Repeat command (high priority check)
            normalized == "repeat" || normalized == "repeat that" ||
                    normalized == "say that again" || normalized == "what did you say" -> {
                VoiceIntent.RepeatLast
            }

            // Location & Heading
            normalized == "where am i" || normalized == "what is my location" ||
                    normalized == "tell me where i am" || normalized == "current location" -> {
                VoiceIntent.WhereAmI
            }

            normalized == "what's my direction" || normalized == "whats my direction" ||
                    normalized == "which way am i facing" || normalized == "compass" ||
                    normalized == "compass heading" || normalized == "direction" -> {
                VoiceIntent.GetCompassDirection
            }

            normalized == "how far" || normalized == "how far is it" ||
                    normalized == "how much farther" || normalized == "distance" ||
                    normalized == "distance left" -> {
                VoiceIntent.HowFar
            }

            // Navigation Commands
            normalized.startsWith("navigate to ") -> {
                val destination = normalized.removePrefix("navigate to ").trim()
                VoiceIntent.StartNavigation(destination)
            }
            normalized.startsWith("take me to ") -> {
                val destination = normalized.removePrefix("take me to ").trim()
                VoiceIntent.StartNavigation(destination)
            }
            normalized.startsWith("go to ") -> {
                val destination = normalized.removePrefix("go to ").trim()
                VoiceIntent.StartNavigation(destination)
            }
            normalized == "navigate" || normalized == "start navigation" -> {
                VoiceIntent.StartNavigation("")
            }
            normalized == "stop navigation" || normalized == "cancel navigation" ||
                    normalized == "end route" || normalized == "stop route" -> {
                VoiceIntent.StopNavigation
            }

            // Camera & Scene Assistance
            normalized == "what is in front of me" || normalized == "describe" ||
                    normalized == "describe scene" || normalized == "describe the scene" ||
                    normalized == "what do you see" || normalized == "describe surroundings" -> {
                VoiceIntent.DescribeScene
            }

            normalized.startsWith("find my ") -> {
                val target = normalized.removePrefix("find my ").trim()
                VoiceIntent.FindObject(target)
            }
            normalized.startsWith("find ") && !normalized.contains("pharmacy") && !normalized.contains("hospital") && !normalized.contains("atm") -> {
                val target = normalized.removePrefix("find ").trim()
                VoiceIntent.FindObject(target)
            }

            // Reader & OCR Commands
            normalized == "read this" || normalized == "read" ||
                    normalized == "read document" || normalized == "read text" ||
                    normalized == "scan text" || normalized == "read sign" ||
                    normalized == "read everything" -> {
                VoiceIntent.ReadText
            }
            normalized == "stop reading" || normalized == "silence reader" || normalized == "stop reader" -> {
                VoiceIntent.StopReading
            }
            normalized == "pause reading" || normalized == "pause" -> {
                VoiceIntent.PauseReading
            }
            normalized == "resume reading" || normalized == "resume" -> {
                VoiceIntent.ResumeReading
            }

            // Nearby Places
            normalized == "what's around me" || normalized == "whats around me" ||
                    normalized == "what is around me" || normalized == "nearby" ||
                    normalized == "nearby places" -> {
                VoiceIntent.FindNearby("")
            }
            normalized.contains("pharmacy") -> VoiceIntent.FindNearby("pharmacy")
            normalized.contains("hospital") || normalized.contains("clinic") -> VoiceIntent.FindNearby("hospital")
            normalized.contains("atm") || normalized.contains("bank") -> VoiceIntent.FindNearby("bank")
            normalized.contains("restaurant") || normalized.contains("food") -> VoiceIntent.FindNearby("restaurant")
            normalized.contains("bus stop") || normalized.contains("train station") || normalized.contains("transit") -> VoiceIntent.FindNearby("transit")
            normalized.contains("grocery") || normalized.contains("supermarket") -> VoiceIntent.FindNearby("grocery")
            normalized.contains("police") -> VoiceIntent.FindNearby("police")

            // Safety & Emergency
            normalized == "emergency" || normalized == "sos" || normalized == "help me" -> {
                VoiceIntent.TriggerSos
            }
            normalized == "cancel sos" || normalized == "cancel emergency" || normalized == "im okay" || normalized == "i'm okay" -> {
                VoiceIntent.CancelSos
            }
            normalized == "call my emergency contact" || normalized == "call emergency contact" ||
                    normalized == "call contact" || normalized == "call help" -> {
                VoiceIntent.CallEmergencyContact
            }

            // Location Sharing
            normalized == "share my location" || normalized == "start sharing location" ||
                    normalized == "share location" -> {
                VoiceIntent.ShareLocation
            }
            normalized == "stop sharing location" || normalized == "stop sharing" ||
                    normalized == "cancel location sharing" -> {
                VoiceIntent.StopSharingLocation
            }

            // Settings & System
            normalized == "open settings" || normalized == "settings" -> {
                VoiceIntent.OpenSettings
            }
            normalized == "help" || normalized == "what can you do" ||
                    normalized == "what can i say" || normalized == "commands" -> {
                VoiceIntent.Help
            }

            else -> VoiceIntent.Unknown(rawTranscript)
        }
    }
}
