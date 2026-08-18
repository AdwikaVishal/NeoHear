package com.neohear.audio.capture

import com.neohear.audio.waveform.WaveformFixtureLoader
import org.junit.Test
import org.junit.Assert.*

class ReplayOaeCaptureTest {
    @Test
    fun fixture_loads_and_emits_samples() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_1.json")
        val replay = com.neohear.audio.pipeline.ReplayResponseCapture(
            waveform = fixture.waveform,
            sampleRateHz = fixture.metadata.sampleRateHz,
            numRepetitions = 2,
            noiseStdDev = 0.0f,
            seed = 1L
        )

        replay.startCapture()
        val rep1 = replay.captureRepetition()
        val rep2 = replay.captureRepetition()
        replay.stopCapture()

        assertNotNull(rep1)
        assertNotNull(rep2)
        assertEquals(fixture.waveform.size, rep1!!.samples.size)
        assertEquals(fixture.metadata.sampleRateHz, rep1.sampleRateHz)
    }
}
