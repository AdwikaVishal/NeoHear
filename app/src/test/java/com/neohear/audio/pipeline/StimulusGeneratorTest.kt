package com.neohear.audio.pipeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StimulusGeneratorTest {

    @Test
    fun pureClick_stimulusLength() {
        val samples = StimulusGenerator.pureGenerateClickStimulus(24000, 480)
        assertEquals(480, samples.size)
    }

    @Test
    fun pureClick_firstSampleIsOne() {
        val samples = StimulusGenerator.pureGenerateClickStimulus(24000, 100)
        assertEquals(1.0f, samples[0], 1e-6f)
    }

    @Test
    fun pureClick_restAreZeros() {
        val samples = StimulusGenerator.pureGenerateClickStimulus(24000, 100)
        for (i in 1 until samples.size) {
            assertEquals("Sample $i should be zero", 0.0f, samples[i], 1e-6f)
        }
    }

    @Test
    fun pureClick_emptyBuffer() {
        val samples = StimulusGenerator.pureGenerateClickStimulus(24000, 0)
        assertEquals(0, samples.size)
    }

    @Test
    fun pureDp_stimulusLength() {
        val samples = StimulusGenerator.pureGenerateDpStimulus(
            sampleRateHz = 24000, numSamples = 480,
            f1Hz = 1000.0, f2Hz = 1220.0,
            amplitude1 = 0.5f, amplitude2 = 0.5f
        )
        assertEquals(480, samples.size)
    }

    @Test
    fun pureDp_sinusoidalOutput() {
        val sampleRate = 24000
        val numSamples = 480
        val f1 = 1000.0
        val a1 = 1.0f

        val samples = StimulusGenerator.pureGenerateDpStimulus(
            sampleRateHz = sampleRate, numSamples = numSamples,
            f1Hz = f1, f2Hz = 2000.0,
            amplitude1 = a1, amplitude2 = 0.0f
        )

        // Verify first sample: sin(2π * 1000 * 0/24000) = sin(0) = 0
        assertEquals(0.0f, samples[0], 1e-6f)

        // Verify sample at 1/4 period of 1000Hz: t = 1/(4*1000) = 0.00025s
        // At sample index: 0.00025 * 24000 = 6
        // sin(2π * 1000 * 6/24000) = sin(π/2) = 1.0
        assertEquals(1.0f, samples[6], 0.01f)
    }

    @Test
    fun pureDp_twoTonesSum() {
        val samples = StimulusGenerator.pureGenerateDpStimulus(
            sampleRateHz = 24000, numSamples = 480,
            f1Hz = 1000.0, f2Hz = 1220.0,
            amplitude1 = 0.5f, amplitude2 = 0.5f
        )

        // At t=0 both sin terms are 0
        assertEquals(0.0f, samples[0], 1e-6f)

        // Check amplitude doesn't exceed sum of individual amplitudes
        val maxVal = samples.maxOrNull()!!
        assertTrue("Max value should be <= a1 + a2", maxVal <= 1.0f + 1e-6f)
    }

    @Test
    fun pureDp_zeroAmplitude() {
        val samples = StimulusGenerator.pureGenerateDpStimulus(
            sampleRateHz = 24000, numSamples = 100,
            f1Hz = 1000.0, f2Hz = 1220.0,
            amplitude1 = 0.0f, amplitude2 = 0.0f
        )
        for (s in samples) {
            assertEquals(0.0f, s, 1e-6f)
        }
    }

    @Test
    fun generateClick_delegatesCorrectly() {
        val samples = StimulusGenerator.generateClickStimulus(24000, 20.0)
        // 24000 * 20 / 1000 = 480
        assertEquals(480, samples.size)
        assertEquals(1.0f, samples[0], 1e-6f)
    }

    @Test
    fun generateDp_delegatesCorrectly() {
        val samples = StimulusGenerator.generateDpStimulus(
            sampleRateHz = 24000, f1Hz = 1000.0, f2Hz = 1220.0,
            durationMs = 20.0, amplitude1 = 0.5f, amplitude2 = 0.5f
        )
        assertEquals(480, samples.size)
    }
}
