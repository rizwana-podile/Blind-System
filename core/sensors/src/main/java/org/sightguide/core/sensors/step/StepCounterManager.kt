package org.sightguide.core.sensors.step

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Step detector tracking pedestrian movement during dead reckoning and walking sessions.
 */
class StepCounterManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val stepDetector = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    fun startTracking(): Boolean {
        if (sensorManager == null || stepDetector == null) return false
        val registered = sensorManager.registerListener(
            this,
            stepDetector,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        if (registered) {
            _isTracking.value = true
        }
        return registered
    }

    fun stopTracking() {
        sensorManager?.unregisterListener(this)
        _isTracking.value = false
    }

    fun resetSteps() {
        _stepCount.value = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            _stepCount.value += 1
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Step detector accuracy changes do not require calibration intervention
    }
}
