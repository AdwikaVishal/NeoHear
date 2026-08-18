package com.neohear.demo

import kotlinx.coroutines.flow.MutableStateFlow

object DemoScenarioManager {
    val currentScenario: MutableStateFlow<DemoScenario?> = MutableStateFlow(null)
}
