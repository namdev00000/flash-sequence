package com.flashdance.app

enum class PatternType { COMBO, FLASH_ONLY }

/** One on/off beat in a pattern, in milliseconds. */
data class PatternStep(val onMs: Long, val offMs: Long)

data class DancePattern(
    val id: Int,
    val name: String,
    val type: PatternType,
    val steps: List<PatternStep>
)
