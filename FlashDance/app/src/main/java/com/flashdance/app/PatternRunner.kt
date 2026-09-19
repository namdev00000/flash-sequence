package com.flashdance.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PatternRunner(
    private val flash: FlashController,
    private val vibrator: VibratorController
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    var runningPatternId: Int? = null
        private set

    val isRunning: Boolean get() = job?.isActive == true

    fun start(pattern: DancePattern, vibrationEnabled: Boolean) {
        stop()
        runningPatternId = pattern.id
        job = scope.launch {
            while (isActive) {
                for (step in pattern.steps) {
                    if (!isActive) break

                    if (step.onMs > 0) {
                        flash.setTorch(true)
                        if (pattern.type == PatternType.COMBO && vibrationEnabled) {
                            vibrator.vibrate(step.onMs)
                        }
                        delay(step.onMs)
                        flash.setTorch(false)
                    }

                    if (step.offMs > 0) {
                        delay(step.offMs)
                    }
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        runningPatternId = null
        flash.setTorch(false)
        vibrator.cancel()
    }
}
