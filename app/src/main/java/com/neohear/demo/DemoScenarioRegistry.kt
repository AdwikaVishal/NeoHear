package com.neohear.demo

import com.neohear.data.entity.Ear

object DemoScenarioRegistry {
    val scenarios: List<DemoScenario> = listOf(
        DemoScenario(
            id = "normal_pass",
            name = "Normal Response",
            description = "Clear synthetic OAE response expected to meet the prototype screening criterion.",
            type = DemoScenarioType.PASS,
            leftStage1Fixture = "clear_pass_1",
            rightStage1Fixture = "clear_pass_2",
            leftStage2Fixture = "clear_pass_1",
            rightStage2Fixture = "clear_pass_2",
            noiseMode = DemoNoiseMode.NORMAL
        ),
        DemoScenario(
            id = "weak_refer",
            name = "Weak Response",
            description = "Weak synthetic response expected to fail the prototype screening criterion.",
            type = DemoScenarioType.REFER,
            leftStage1Fixture = "clear_refer_1",
            rightStage1Fixture = "clear_refer_2",
            leftStage2Fixture = "clear_refer_1",
            rightStage2Fixture = "clear_refer_2",
            noiseMode = DemoNoiseMode.NORMAL
        ),
        DemoScenario(
            id = "borderline_repeat",
            name = "Borderline Response",
            description = "Borderline synthetic response intended to demonstrate the repeat pathway.",
            type = DemoScenarioType.REPEAT,
            leftStage1Fixture = "borderline_1",
            rightStage1Fixture = "borderline_2",
            leftStage2Fixture = "borderline_1",
            rightStage2Fixture = "borderline_2",
            noiseMode = DemoNoiseMode.NORMAL
        ),
        DemoScenario(
            id = "high_noise",
            name = "High Ambient Noise",
            description = "Simulates an environment where acoustic noise prevents a valid screening attempt.",
            type = DemoScenarioType.HIGH_NOISE,
            noiseMode = DemoNoiseMode.HIGH
        ),
        DemoScenario(
            id = "two_stage_refer",
            name = "Two-Stage Referral",
            description = "Demonstrates Stage 1 REFER followed by Stage 2 REFER and automatic referral creation.",
            type = DemoScenarioType.TWO_STAGE_REFER,
            leftStage1Fixture = "clear_refer_1",
            rightStage1Fixture = "clear_refer_2",
            leftStage2Fixture = "clear_refer_1",
            rightStage2Fixture = "clear_refer_2",
            noiseMode = DemoNoiseMode.NORMAL
        )
    )

    fun findById(id: String): DemoScenario? = scenarios.find { it.id == id }
}
