package com.neohear.demo

import com.neohear.data.entity.Ear

enum class DemoScenarioType { PASS, REFER, REPEAT, HIGH_NOISE, TWO_STAGE_REFER }

enum class DemoNoiseMode { NORMAL, HIGH }

data class DemoScenario(
    val id: String,
    val name: String,
    val description: String,
    val type: DemoScenarioType,
    // fixture names for left/right ears at stage 1 and stage 2. Null for noise-only scenarios.
    val leftStage1Fixture: String? = null,
    val rightStage1Fixture: String? = null,
    val leftStage2Fixture: String? = null,
    val rightStage2Fixture: String? = null,
    val noiseMode: DemoNoiseMode = DemoNoiseMode.NORMAL
)

fun DemoScenario.fixtureFor(ear: Ear, stage: Int): String? {
    return when (stage) {
        1 -> if (ear == Ear.L) leftStage1Fixture else rightStage1Fixture
        2 -> if (ear == Ear.L) leftStage2Fixture ?: leftStage1Fixture else rightStage2Fixture ?: rightStage1Fixture
        else -> null
    }
}
