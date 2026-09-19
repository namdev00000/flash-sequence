package com.example.flashdance

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.tabs.TabLayout

class MainActivity : ComponentActivity() {

    private lateinit var cameraManager: CameraManager
    private var torchCameraId: String? = null

    private lateinit var vibrationSwitch: MaterialSwitch
    private lateinit var tabLayout: TabLayout
    private lateinit var patternsContainer: LinearLayout
    private lateinit var stopButton: MaterialButton
    private lateinit var statusText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var runningPattern: Pattern? = null
    private var runningIndex = 0
    private var sequenceRunnable: Runnable? = null

    private val CAMERA_REQUEST = 1001

    private data class Step(
        val onMs: Long,
        val offMs: Long,
        val vibrate: Boolean = false
    )

    private data class Pattern(
        val name: String,
        val description: String,
        val steps: List<Step>
    )

    private val dancePatterns = listOf(
        Pattern("Heartbeat", "Short-short, then a pause", listOf(
            Step(140, 110, true), Step(140, 520, true)
        )),
        Pattern("Double Beat", "Two quick beats", listOf(
            Step(180, 130, true), Step(180, 500, true)
        )),
        Pattern("Triple Beat", "Three rhythmic flashes", listOf(
            Step(130, 100, true), Step(130, 100, true), Step(130, 500, true)
        )),
        Pattern("Fast Pulse", "Rapid repeating pulse", listOf(
            Step(90, 90, true), Step(90, 90, true), Step(90, 90, true), Step(90, 450, true)
        )),
        Pattern("Slow Pulse", "Relaxed steady pulse", listOf(
            Step(280, 420, true)
        )),
        Pattern("SOS Style", "Three short, three long, three short", listOf(
            Step(120, 100, true), Step(120, 100, true), Step(120, 150, true),
            Step(420, 100, true), Step(420, 100, true), Step(420, 150, true),
            Step(120, 100, true), Step(120, 100, true), Step(120, 600, true)
        )),
        Pattern("Wave", "Increasing and decreasing rhythm", listOf(
            Step(100, 100, true), Step(150, 100, true), Step(200, 100, true),
            Step(250, 180, true), Step(200, 100, true), Step(150, 100, true), Step(100, 500, true)
        )),
        Pattern("Strobe", "Fast energetic sequence", listOf(
            Step(70, 70, true), Step(70, 70, true), Step(70, 70, true),
            Step(70, 70, true), Step(70, 70, true), Step(70, 350, true)
        )),
        Pattern("March", "Even marching rhythm", listOf(
            Step(180, 220, true), Step(180, 220, true), Step(180, 550, true)
        )),
        Pattern("Finale", "A short build-up and final flash", listOf(
            Step(100, 100, true), Step(160, 100, true), Step(220, 100, true),
            Step(300, 150, true), Step(500, 700, true)
        ))
    )

    private val flashPatterns = dancePatterns.map { pattern ->
        pattern.copy(
            name = pattern.name,
            description = pattern.description,
            steps = pattern.steps.map { it.copy(vibrate = false) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        bindViews()
        findTorchCamera()
        setupTabs()
        renderPatterns(dancePatterns)

        stopButton.setOnClickListener { stopPattern() }

        vibrationSwitch.setOnCheckedChangeListener { _, _ ->
            if (runningPattern != null) {
                stopPattern()
            }
        }

        statusText.text = getString(R.string.ready)
    }

    private fun bindViews() {
        vibrationSwitch = findViewById(R.id.vibrationSwitch)
        tabLayout = findViewById(R.id.tabLayout)
        patternsContainer = findViewById(R.id.patternsContainer)
        stopButton = findViewById(R.id.stopButton)
        statusText = findViewById(R.id.statusText)
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.dance_vibe))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.flash_only))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                stopPattern()
                if (tab.position == 0) {
                    renderPatterns(dancePatterns)
                } else {
                    renderPatterns(flashPatterns)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: Tab) = Unit
        })
    }

    private fun renderPatterns(patterns: List<Pattern>) {
        patternsContainer.removeAllViews()

        patterns.forEachIndexed { index, pattern ->
            val card = layoutInflater.inflate(
                R.layout.item_pattern,
                patternsContainer,
                false
            )

            val number = card.findViewById<TextView>(R.id.patternNumber)
            val name = card.findViewById<TextView>(R.id.patternName)
            val description = card.findViewById<TextView>(R.id.patternDescription)

            number.text = String.format("%02d", index + 1)
            name.text = pattern.name
            description.text = pattern.description

            card.setOnClickListener {
                if (runningPattern?.name == pattern.name) {
                    stopPattern()
                } else {
                    startPattern(pattern)
                }
            }

            patternsContainer.addView(card)
        }
    }

    private fun findTorchCamera() {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)

                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    torchCameraId = cameraId
                    return
                }
            }

            // Fallback: use any camera with a flash.
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) {
                    torchCameraId = cameraId
                    return
                }
            }
        } catch (_: Exception) {
            torchCameraId = null
        }
    }

    private fun startPattern(pattern: Pattern) {
        if (torchCameraId == null) {
            Toast.makeText(this, R.string.no_flash, Toast.LENGTH_LONG).show()
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
            return
        }

        stopPattern()

        runningPattern = pattern
        runningIndex = 0
        statusText.text = getString(R.string.playing_format, pattern.name)
        stopButton.isEnabled = true

        runNextStep()
    }

    private fun runNextStep() {
        val pattern = runningPattern ?: return

        if (runningIndex >= pattern.steps.size) {
            runningIndex = 0
        }

        val step = pattern.steps[runningIndex]
        setTorch(true)

        if (vibrationSwitch.isChecked && step.vibrate) {
            vibrate(step.onMs)
        }

        sequenceRunnable = Runnable {
            setTorch(false)

            handler.postDelayed({
                runningIndex++
                runNextStep()
            }, step.offMs)
        }

        handler.postDelayed(sequenceRunnable!!, step.onMs)
    }

    private fun setTorch(enabled: Boolean) {
        val id = torchCameraId ?: return
        try {
            cameraManager.setTorchMode(id, enabled)
        } catch (_: Exception) {
            // Device/OEM can temporarily reject torch requests.
        }
    }

    private fun vibrate(duration: Long) {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= 31) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration.coerceAtMost(1000L),
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }

    private fun stopPattern() {
        sequenceRunnable?.let { handler.removeCallbacks(it) }
        handler.removeCallbacksAndMessages(null)

        setTorch(false)

        runningPattern = null
        runningIndex = 0
        stopButton.isEnabled = false
        statusText.text = getString(R.string.ready)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, R.string.camera_permission_granted, Toast.LENGTH_SHORT).show()
        } else if (requestCode == CAMERA_REQUEST) {
            Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        super.onStop()
        // Safety: never leave the torch running after the app is no longer visible.
        stopPattern()
    }

    override fun onDestroy() {
        stopPattern()
        super.onDestroy()
    }
}
