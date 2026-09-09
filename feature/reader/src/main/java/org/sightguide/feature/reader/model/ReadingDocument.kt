package org.sightguide.feature.reader.model

/**
 * Parsed document structure containing raw OCR text, categorized document type,
 * and key extracted highlights for rapid comprehension.
 */
data class ReadingDocument(
    val rawText: String,
    val documentType: DocumentType,
    val textBlocks: List<String>,
    val keyHighlight: String? = null
)

enum class DocumentType(val spokenName: String) {
    RECEIPT("Receipt"),
    MENU("Restaurant Menu"),
    LABEL("Product Packaging or Label"),
    SIGN("Sign or Notice"),
    GENERAL_DOCUMENT("Document")
}
