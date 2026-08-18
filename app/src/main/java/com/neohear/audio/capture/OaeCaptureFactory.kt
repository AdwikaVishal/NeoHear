package com.neohear.audio.capture

import com.neohear.data.entity.Mode
import com.neohear.data.entity.Ear
import com.neohear.audio.waveform.WaveformFixtureLoader
import com.neohear.audio.pipeline.ReplayResponseCapture

object OaeCaptureFactory {
    fun create(mode: Mode, ear: Ear, stage: Int, demoFixtureName: String? = null): OaeCaptureSource {
        return if (mode == Mode.DEMO) {
            // Determine fixture name
            val fixture = demoFixtureName ?: when (ear) {
                Ear.L -> "clear_pass_1"
                Ear.R -> "clear_pass_2"
            }
            val fixtureObj = WaveformFixtureLoader.loadFromResources("reference_waveforms/$fixture.json")
            ReplayResponseCapture(
                waveform = fixtureObj.waveform,
                sampleRateHz = fixtureObj.metadata.sampleRateHz,
                numRepetitions = 5,
                noiseStdDev = 0.01f
            )
        } else {
            RealOaeCapture()
        }
    }
}
