package com.hellguy39.rotationsensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hellguy39.rotationsensor.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor

    private lateinit var sensorEventListener: SensorEventListener

    private val df = DecimalFormat("#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initFeatures()
    }

    private fun initFeatures() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {

                val rotationMatrix = FloatArray(16)
                val remappedRotationMatrix = FloatArray(16)

                SensorManager.getRotationMatrixFromVector(rotationMatrix, p0?.values)
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix
                )

                val orientations = FloatArray(3)
                SensorManager.getOrientation(remappedRotationMatrix, orientations)

                for (n in 0..2) {
                    orientations[n] = (Math.toDegrees(orientations[n].toDouble()).toFloat())
                }

                _binding.tvDegrees.text = df.format(orientations[2])
                _binding.rotatableView.rotation = -orientations[2]

            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(sensorEventListener)
    }

}