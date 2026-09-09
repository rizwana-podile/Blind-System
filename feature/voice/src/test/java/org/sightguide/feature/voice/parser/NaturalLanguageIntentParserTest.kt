package org.sightguide.feature.voice.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sightguide.feature.voice.model.VoiceIntent

class NaturalLanguageIntentParserTest {

    @Test
    fun parse_whereAmI_returnsWhereAmI() {
        assertEquals(VoiceIntent.WhereAmI, NaturalLanguageIntentParser.parse("Where am I?"))
        assertEquals(VoiceIntent.WhereAmI, NaturalLanguageIntentParser.parse("what is my location"))
        assertEquals(VoiceIntent.WhereAmI, NaturalLanguageIntentParser.parse("Tell me where I am!"))
    }

    @Test
    fun parse_compass_returnsCompassDirection() {
        assertEquals(VoiceIntent.GetCompassDirection, NaturalLanguageIntentParser.parse("What's my direction?"))
        assertEquals(VoiceIntent.GetCompassDirection, NaturalLanguageIntentParser.parse("which way am i facing"))
        assertEquals(VoiceIntent.GetCompassDirection, NaturalLanguageIntentParser.parse("compass"))
    }

    @Test
    fun parse_navigation_extractsDestination() {
        val intent1 = NaturalLanguageIntentParser.parse("Navigate to Central Station")
        assertTrue(intent1 is VoiceIntent.StartNavigation)
        assertEquals("central station", (intent1 as VoiceIntent.StartNavigation).destination)

        val intent2 = NaturalLanguageIntentParser.parse("Take me to the pharmacy")
        assertTrue(intent2 is VoiceIntent.StartNavigation)
        assertEquals("the pharmacy", (intent2 as VoiceIntent.StartNavigation).destination)
    }

    @Test
    fun parse_describeScene_returnsDescribeScene() {
        assertEquals(VoiceIntent.DescribeScene, NaturalLanguageIntentParser.parse("What is in front of me?"))
        assertEquals(VoiceIntent.DescribeScene, NaturalLanguageIntentParser.parse("Describe the scene"))
    }

    @Test
    fun parse_findObject_extractsTarget() {
        val intent = NaturalLanguageIntentParser.parse("Find my bottle")
        assertTrue(intent is VoiceIntent.FindObject)
        assertEquals("bottle", (intent as VoiceIntent.FindObject).targetObject)
    }

    @Test
    fun parse_readerCommands_returnsReaderIntents() {
        assertEquals(VoiceIntent.ReadText, NaturalLanguageIntentParser.parse("Read this!"))
        assertEquals(VoiceIntent.ReadText, NaturalLanguageIntentParser.parse("Scan text"))
        assertEquals(VoiceIntent.StopReading, NaturalLanguageIntentParser.parse("Stop reading"))
        assertEquals(VoiceIntent.PauseReading, NaturalLanguageIntentParser.parse("Pause"))
        assertEquals(VoiceIntent.ResumeReading, NaturalLanguageIntentParser.parse("Resume"))
    }

    @Test
    fun parse_sosCommands_returnsSosIntents() {
        assertEquals(VoiceIntent.TriggerSos, NaturalLanguageIntentParser.parse("Emergency!"))
        assertEquals(VoiceIntent.TriggerSos, NaturalLanguageIntentParser.parse("SOS"))
        assertEquals(VoiceIntent.CancelSos, NaturalLanguageIntentParser.parse("Cancel SOS"))
        assertEquals(VoiceIntent.CallEmergencyContact, NaturalLanguageIntentParser.parse("Call my emergency contact"))
    }

    @Test
    fun parse_repeatLast_returnsRepeatLast() {
        assertEquals(VoiceIntent.RepeatLast, NaturalLanguageIntentParser.parse("Repeat that"))
        assertEquals(VoiceIntent.RepeatLast, NaturalLanguageIntentParser.parse("Say that again"))
    }
}
