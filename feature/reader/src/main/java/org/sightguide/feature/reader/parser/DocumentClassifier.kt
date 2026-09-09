package org.sightguide.feature.reader.parser

import org.sightguide.feature.reader.model.DocumentType
import org.sightguide.feature.reader.model.ReadingDocument
import java.util.Locale

/**
 * Intelligent on-device document categorizer and key-information extractor.
 */
object DocumentClassifier {

    private val RECEIPT_TOTAL_REGEX = Regex("""(?i)(?:total|amount due|balance due)\s*[:$]?\s*(\$?\d+\.\d{2})""")
    private val EXPIRATION_REGEX = Regex("""(?i)(?:exp|best before|use by)\s*[:.]?\s*([0-9]{1,2}[/-][0-9]{1,2}[/-][0-9]{2,4})""")

    fun classify(rawText: String, blocks: List<String>): ReadingDocument {
        val lower = rawText.lowercase(Locale.ROOT)

        val docType: DocumentType
        var highlight: String? = null

        when {
            lower.contains("total") || lower.contains("subtotal") || lower.contains("tax") || lower.contains("receipt") -> {
                docType = DocumentType.RECEIPT
                val match = RECEIPT_TOTAL_REGEX.find(rawText)
                if (match != null) {
                    highlight = "Total: ${match.groupValues[1]}"
                }
            }
            lower.contains("appetizer") || lower.contains("entree") || lower.contains("dessert") ||
                    lower.contains("beverage") || lower.contains("menu") -> {
                docType = DocumentType.MENU
            }
            lower.contains("nutrition facts") || lower.contains("ingredients") ||
                    lower.contains("net wt") || lower.contains("exp") || lower.contains("best before") -> {
                docType = DocumentType.LABEL
                val match = EXPIRATION_REGEX.find(rawText)
                if (match != null) {
                    highlight = "Expiration: ${match.groupValues[1]}"
                }
            }
            rawText.length < 80 && (lower.contains("exit") || lower.contains("caution") ||
                    lower.contains("entrance") || lower.contains("stop") || lower.contains("floor") ||
                    lower.contains("room") || lower.contains("street") || lower.contains("avenue")) -> {
                docType = DocumentType.SIGN
                highlight = rawText.trim()
            }
            else -> {
                docType = DocumentType.GENERAL_DOCUMENT
            }
        }

        return ReadingDocument(
            rawText = rawText,
            documentType = docType,
            textBlocks = blocks,
            keyHighlight = highlight
        )
    }
}
