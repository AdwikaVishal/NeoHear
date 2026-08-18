package com.neohear.demo

import org.junit.Test
import org.junit.Assert.*

class DemoScenarioRegistryTest {
    @Test
    fun registry_contains_required_scenarios() {
        val ids = DemoScenarioRegistry.scenarios.map { it.id }.toSet()
        assertTrue(ids.contains("normal_pass"))
        assertTrue(ids.contains("weak_refer"))
        assertTrue(ids.contains("borderline_repeat"))
        assertTrue(ids.contains("high_noise"))
        assertTrue(ids.contains("two_stage_refer"))
    }

    @Test
    fun fixtures_map_exist_for_non_noise_scenarios() {
        DemoScenarioRegistry.scenarios.filter { it.type != DemoScenarioType.HIGH_NOISE }.forEach { s ->
            // At least one fixture should be present for stage1
            assertNotNull("Scenario ${s.id} must have a fixture for left ear", s.leftStage1Fixture)
        }
    }
}
