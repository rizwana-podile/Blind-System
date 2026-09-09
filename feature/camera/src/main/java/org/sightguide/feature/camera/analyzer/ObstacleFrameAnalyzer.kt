package org.sightguide.feature.camera.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import org.sightguide.feature.camera.model.ConfidenceLevel
import org.sightguide.feature.camera.model.DetectedObstacle
import org.sightguide.feature.camera.model.SpatialPosition

/**
 * CameraX ImageAnalysis analyzer running ML Kit on-device object detection.
 * Employs non-blocking execution to keep latency minimal and preserve battery life.
 */
class ObstacleFrameAnalyzer(
    private val onObstaclesDetected: (List<DetectedObstacle>) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .build()

    private val detector = ObjectDetection.getClient(options)
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
        val frameWidth = imageProxy.width.toFloat()
        val frameHeight = imageProxy.height.toFloat()

        detector.process(image)
            .addOnSuccessListener { detectedObjects ->
                val list = detectedObjects.mapNotNull { obj ->
                    val primaryLabel = obj.labels.firstOrNull()?.text ?: "Object"
                    val rawConfidence = obj.labels.firstOrNull()?.confidence ?: 0.5f

                    val confidenceLevel = when {
                        rawConfidence >= 0.75f -> ConfidenceLevel.DETECTED
                        rawConfidence >= 0.45f -> ConfidenceLevel.PROBABLY_DETECTED
                        else -> ConfidenceLevel.UNCERTAIN
                    }

                    // Compute horizontal centroid
                    val box = obj.boundingBox
                    val centerX = box.centerX().toFloat()
                    val spatialPos = when {
                        centerX < frameWidth * 0.35f -> SpatialPosition.LEFT
                        centerX > frameWidth * 0.65f -> SpatialPosition.RIGHT
                        else -> SpatialPosition.CENTER
                    }

                    // Approximate distance from bounding box relative scale (heuristic for pedestrian safety)
                    val boxHeightRatio = box.height().toFloat() / frameHeight
                    val estimatedDistance = (1.8f / boxHeightRatio.coerceAtLeast(0.1f)).coerceIn(0.5f, 6.0f)
                    val isHazard = estimatedDistance < 1.5f && spatialPos == SpatialPosition.CENTER

                    DetectedObstacle(
                        label = primaryLabel,
                        confidence = confidenceLevel,
                        spatialPosition = spatialPos,
                        approximateDistanceMeters = estimatedDistance,
                        isHazard = isHazard
                    )
                }
                onObstaclesDetected(list)
            }
            .addOnFailureListener {
                onObstaclesDetected(emptyList())
            }
            .addOnCompleteListener {
                isBusy = false
                imageProxy.close()
            }
    }
}
