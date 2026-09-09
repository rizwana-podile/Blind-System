package org.sightguide.core.sensors.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.sightguide.core.common.model.CompassDirection
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Real-time sensor fusion compass heading provider.
 * Blends 3-axis accelerometer and magnetometer vectors, applies circular low-pass filtering
 * across the 0/360° boundary, and emits smoothed azimuth angles and cardinal directions.
 */
class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private val _headingDegrees = MutableStateFlow(0f)
    val headingDegrees: StateFlow<Float> = _headingDegrees.asStateFlow()

    private val _compassDirection = MutableStateFlow(CompassDirection.NORTH)
    val compassDirection: StateFlow<CompassDirection> = _compassDirection.asStateFlow()

    private val _accuracy = MutableStateFlow(SensorAccuracy.HIGH)
    val accuracy: StateFlow<SensorAccuracy> = _accuracy.asStateFlow()

    private var smoothedSin = 0.0
    private var smoothedCos = 1.0
    private val filterAlpha = 0.15 // Low-pass factor for walking stability

    fun startListening(): Boolean {
        if (sensorManager == null || accelerometer == null || magnetometer == null) {
            return false
        }
        val accRegistered = sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        val magRegistered = sensorManager.registerListener(
            this,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )
        return accRegistered && magRegistered
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
        hasGravity = false
        hasGeomagnetic = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                lowPass(event.values, gravity)
                hasGravity = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                lowPass(event.values, geomagnetic)
                hasGeomagnetic = true
            }
        }

        if (hasGravity && hasGeomagnetic) {
            val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientation)
                val rawAzimuthRadians = orientation[0].toDouble()

                // Circular low-pass filter avoiding 0-360 discontinuity jump
                smoothedSin = (1 - filterAlpha) * smoothedSin + filterAlpha * sin(rawAzimuthRadians)
                smoothedCos = (1 - filterAlpha) * smoothedCos + filterAlpha * cos(rawAzimuthRadians)

                val smoothedAngleRad = atan2(smoothedSin, smoothedCos)
                var smoothedDegrees = Math.toDegrees(smoothedAngleRad).toFloat()
                smoothedDegrees = (smoothedDegrees + 360f) % 360f

                _headingDegrees.value = smoothedDegrees
                _compassDirection.value = CompassDirection.fromAzimuth(smoothedDegrees)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracyLevel: Int) {
        if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            _accuracy.value = when (accuracyLevel) {
                SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
                else -> SensorAccuracy.UNKNOWN
            }
        }
    }

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.25f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }
}

enum class SensorAccuracy(val spokenAdvice: String) {
    HIGH("Compass calibrated"),
    MEDIUM("Compass accuracy moderate"),
    LOW("Compass accuracy low. Please wave your phone in a figure-8 to calibrate"),
    UNRELIABLE("Compass uncalibrated. Wave device in figure-8 motion"),
    UNKNOWN("Calibrating sensors")
}
