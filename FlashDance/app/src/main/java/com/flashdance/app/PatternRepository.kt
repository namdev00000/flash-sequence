package com.flashdance.app

object PatternRepository {

    // Flash + Vibration patterns
    val comboPatterns: List<DancePattern> = listOf(
        DancePattern(1, "Disco Pulse", PatternType.COMBO, repeat(6, 150, 150)),
        DancePattern(
            2, "Heartbeat", PatternType.COMBO,
            loop(3, listOf(PatternStep(120, 120), PatternStep(120, 600)))
        ),
        DancePattern(3, "Strobe Fast", PatternType.COMBO, repeat(10, 60, 60)),
        DancePattern(4, "Slow Wave", PatternType.COMBO, repeat(5, 500, 500)),
        DancePattern(
            5, "Techno Beat", PatternType.COMBO,
            loop(4, listOf(PatternStep(200, 100), PatternStep(200, 100), PatternStep(200, 400)))
        ),
        DancePattern(
            6, "Party Flash", PatternType.COMBO,
            loop(4, listOf(PatternStep(100, 50), PatternStep(100, 50), PatternStep(100, 300)))
        ),
        DancePattern(7, "Rapid Fire", PatternType.COMBO, repeat(12, 40, 40)),
        DancePattern(
            8, "Wave Pulse", PatternType.COMBO,
            loop(3, listOf(PatternStep(80, 80), PatternStep(160, 160), PatternStep(240, 240), PatternStep(320, 320)))
        ),
        DancePattern(
            9, "Double Tap", PatternType.COMBO,
            loop(4, listOf(PatternStep(100, 80), PatternStep(100, 400)))
        ),
        DancePattern(
            10, "Random Beat", PatternType.COMBO,
            loop(3, listOf(PatternStep(90, 150), PatternStep(300, 60), PatternStep(120, 220), PatternStep(50, 400)))
        )
    )

    // Flash-only patterns (no vibration, regardless of the vibration switch)
    val flashOnlyPatterns: List<DancePattern> = listOf(
        DancePattern(11, "Steady Strobe", PatternType.FLASH_ONLY, repeat(10, 100, 100)),
        DancePattern(12, "SOS Signal", PatternType.FLASH_ONLY, sosSequence()),
        DancePattern(13, "Slow Blink", PatternType.FLASH_ONLY, repeat(6, 600, 600)),
        DancePattern(14, "Fast Blink", PatternType.FLASH_ONLY, repeat(14, 80, 80)),
        DancePattern(
            15, "Police Style", PatternType.FLASH_ONLY,
            loop(4, listOf(PatternStep(100, 100), PatternStep(100, 100), PatternStep(100, 400)))
        ),
        DancePattern(
            16, "Candle Flicker", PatternType.FLASH_ONLY,
            loop(5, listOf(PatternStep(60, 40), PatternStep(90, 60), PatternStep(50, 80), PatternStep(120, 50)))
        ),
        DancePattern(
            17, "Heartbeat Flash", PatternType.FLASH_ONLY,
            loop(4, listOf(PatternStep(100, 100), PatternStep(100, 500)))
        ),
        DancePattern(
            18, "Random Flash", PatternType.FLASH_ONLY,
            loop(3, listOf(PatternStep(70, 200), PatternStep(250, 50), PatternStep(100, 300), PatternStep(180, 90)))
        ),
        DancePattern(
            19, "Double Flash", PatternType.FLASH_ONLY,
            loop(4, listOf(PatternStep(80, 80), PatternStep(80, 400)))
        ),
        DancePattern(
            20, "Wave Flash", PatternType.FLASH_ONLY,
            loop(3, listOf(PatternStep(60, 60), PatternStep(140, 140), PatternStep(220, 220), PatternStep(140, 140), PatternStep(60, 60)))
        )
    )

    private fun repeat(times: Int, onMs: Long, offMs: Long): List<PatternStep> =
        List(times) { PatternStep(onMs, offMs) }

    private fun loop(times: Int, steps: List<PatternStep>): List<PatternStep> =
        List(times) { steps }.flatten()

    private fun sosSequence(): List<PatternStep> {
        val dot = PatternStep(150, 150)
        val dash = PatternStep(450, 150)
        val letterGap = PatternStep(0, 550)
        return listOf(dot, dot, dot, letterGap, dash, dash, dash, letterGap, dot, dot, dot, letterGap)
    }
}
