package com.flashdance.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flashdance.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var flashController: FlashController
    private lateinit var vibratorController: VibratorController
    private lateinit var patternRunner: PatternRunner
    private lateinit var adapter: PatternAdapter

    private var vibrationEnabled = true
    private var showingCombo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        flashController = FlashController(this)
        vibratorController = VibratorController(this)
        patternRunner = PatternRunner(flashController, vibratorController)

        if (!flashController.isAvailable) {
            Toast.makeText(this, R.string.no_flash_message, Toast.LENGTH_LONG).show()
        }

        adapter = PatternAdapter(PatternRepository.comboPatterns) { pattern ->
            onPatternClicked(pattern)
        }
        binding.patternList.layoutManager = LinearLayoutManager(this)
        binding.patternList.adapter = adapter

        binding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            vibrationEnabled = isChecked
        }

        binding.tabCombo.setOnClickListener { selectTab(combo = true) }
        binding.tabFlashOnly.setOnClickListener { selectTab(combo = false) }

        binding.stopButton.setOnClickListener {
            patternRunner.stop()
            adapter.setRunningPatternId(null)
            binding.statusText.text = getString(R.string.status_idle)
        }
    }

    private fun selectTab(combo: Boolean) {
        if (combo == showingCombo) return
        showingCombo = combo

        // Smooth crossfade: fade the list out, swap its data, fade it back in.
        binding.patternList.animate()
            .alpha(0f)
            .setDuration(120)
            .withEndAction {
                adapter.submitList(if (combo) PatternRepository.comboPatterns else PatternRepository.flashOnlyPatterns)
                adapter.setRunningPatternId(patternRunner.runningPatternId)
                binding.patternList.animate().alpha(1f).setDuration(150).start()
            }
            .start()

        binding.tabCombo.setBackgroundResource(if (combo) R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
        binding.tabCombo.setTextColor(getColorCompat(if (combo) R.color.background else R.color.text_secondary))

        binding.tabFlashOnly.setBackgroundResource(if (!combo) R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
        binding.tabFlashOnly.setTextColor(getColorCompat(if (!combo) R.color.background else R.color.text_secondary))
    }

    private fun onPatternClicked(pattern: DancePattern) {
        if (patternRunner.isRunning && patternRunner.runningPatternId == pattern.id) {
            // Tapping the running pattern again stops it
            patternRunner.stop()
            adapter.setRunningPatternId(null)
            binding.statusText.text = getString(R.string.status_idle)
            return
        }

        if (pattern.type == PatternType.FLASH_ONLY && !flashController.isAvailable) {
            Toast.makeText(this, R.string.no_flash_message, Toast.LENGTH_SHORT).show()
            return
        }

        patternRunner.start(pattern, vibrationEnabled)
        adapter.setRunningPatternId(pattern.id)
        binding.statusText.text = pattern.name
    }

    private fun getColorCompat(colorRes: Int) = resources.getColor(colorRes, theme)

    override fun onPause() {
        super.onPause()
        // Never leave the flash or vibration running in the background.
        patternRunner.stop()
        adapter.setRunningPatternId(null)
        binding.statusText.text = getString(R.string.status_idle)
    }
}
