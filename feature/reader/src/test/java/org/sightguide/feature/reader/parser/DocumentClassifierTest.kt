package org.sightguide.feature.reader.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.sightguide.feature.reader.model.DocumentType

class DocumentClassifierTest {

    @Test
    fun classify_receiptText_identifiesReceiptAndTotal() {
        val receipt = """
            WALGREENS STORE #1234
            1 ASPIRIN 325MG $8.99
            1 BANDAGES       $4.50
            SUBTOTAL        $13.49
            TAX              $1.15
            TOTAL: $14.64
            THANK YOU FOR SHOPPING
        """.trimIndent()

        val doc = DocumentClassifier.classify(receipt, receipt.lines())

        assertEquals(DocumentType.RECEIPT, doc.documentType)
        assertNotNull(doc.keyHighlight)
        assertEquals("Total: $14.64", doc.keyHighlight)
    }

    @Test
    fun classify_menuText_identifiesMenu() {
        val menu = """
            DINNER MENU
            APPETIZERS:
            Crispy Calamari $14
            Tomato Bruschetta $11
            ENTREES:
            Grilled Salmon $26
            Mushroom Risotto $22
        """.trimIndent()

        val doc = DocumentClassifier.classify(menu, menu.lines())

        assertEquals(DocumentType.MENU, doc.documentType)
    }

    @Test
    fun classify_signText_identifiesSign() {
        val sign = "CAUTION: WET FLOOR"
        val doc = DocumentClassifier.classify(sign, listOf(sign))

        assertEquals(DocumentType.SIGN, doc.documentType)
        assertEquals("CAUTION: WET FLOOR", doc.keyHighlight)
    }

    @Test
    fun classify_labelText_identifiesPackaging() {
        val label = """
            ORGANIC ALMOND MILK
            NET WT 32 FL OZ
            EXP: 12/25/2026
            INGREDIENTS: ALMONDS, WATER, SEA SALT
        """.trimIndent()

        val doc = DocumentClassifier.classify(label, label.lines())

        assertEquals(DocumentType.LABEL, doc.documentType)
        assertNotNull(doc.keyHighlight)
        assertEquals("Expiration: 12/25/2026", doc.keyHighlight)
    }
}
