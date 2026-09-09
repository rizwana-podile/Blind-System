package org.sightguide.feature.reader.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.sightguide.feature.reader.model.ReadingDocument
import org.sightguide.feature.reader.parser.DocumentClassifier

/**
 * CameraX frame analyzer that performs live on-device OCR using ML Kit Text Recognition.
 */
class TextRecognitionFrameAnalyzer(
    private val onTextRecognized: (ReadingDocument?) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var isBusy = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || isBusy) {
            imageProxy.close()
            return
        }

        isBusy = true
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                if (fullText.isNotBlank()) {
                    val blocks = visionText.textBlocks.map { it.text }
                    val classified = DocumentClassifier.classify(fullText, blocks)
                    onTextRecognized(classified)
                } else {
                    onTextRecognized(null)
                }
            }
            .addOnFailureListener {
                onTextRecognized(null)
            }
            .addOnCompleteListener {
                isBusy = false
                imageProxy.close()
            }
    }
}
