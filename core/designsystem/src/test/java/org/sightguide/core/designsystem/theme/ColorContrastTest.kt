package org.sightguide.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ColorContrastTest {

    /**
     * Standard WCAG 2.2 formula for relative luminance.
     */
    private fun relativeLuminance(color: Color): Double {
        fun channelLuminance(value: Float): Double {
            val v = value.toDouble()
            return if (v <= 0.03928) {
                v / 12.92
            } else {
                ((v + 0.055) / 1.055).pow(2.4)
            }
        }

        val r = channelLuminance(color.red)
        val g = channelLuminance(color.green)
        val b = channelLuminance(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    /**
     * Computes WCAG contrast ratio (L1 + 0.05) / (L2 + 0.05).
     */
    private fun contrastRatio(c1: Color, c2: Color): Double {
        val l1 = relativeLuminance(c1)
        val l2 = relativeLuminance(c2)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun darkTheme_highVisPrimaryOnBackground_exceedsWcagAaa() {
        val ratio = contrastRatio(HighContrastDarkPrimary, HighContrastDarkBackground)
        assertTrue(
            "HighContrastDarkPrimary on HighContrastDarkBackground ratio should exceed 7.0:1 (AAA), but was $ratio",
            ratio >= 7.0
        )
    }

    @Test
    fun darkTheme_textPrimaryOnBackground_exceedsWcagAaa() {
        val ratio = contrastRatio(HighContrastDarkTextPrimary, HighContrastDarkBackground)
        assertTrue(
            "HighContrastDarkTextPrimary on HighContrastDarkBackground ratio should exceed 7.0:1 (AAA), but was $ratio",
            ratio >= 7.0
        )
    }

    @Test
    fun lightTheme_textPrimaryOnBackground_exceedsWcagAaa() {
        val ratio = contrastRatio(HighContrastLightTextPrimary, HighContrastLightBackground)
        assertTrue(
            "HighContrastLightTextPrimary on HighContrastLightBackground ratio should exceed 7.0:1 (AAA), but was $ratio",
            ratio >= 7.0
        )
    }

    @Test
    fun lightTheme_primaryOnBackground_exceedsWcagAaa() {
        val ratio = contrastRatio(HighContrastLightPrimary, HighContrastLightBackground)
        assertTrue(
            "HighContrastLightPrimary on HighContrastLightBackground ratio should exceed 7.0:1 (AAA), but was $ratio",
            ratio >= 7.0
        )
    }
}
