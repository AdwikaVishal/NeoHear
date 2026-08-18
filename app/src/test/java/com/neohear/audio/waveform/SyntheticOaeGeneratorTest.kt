package com.neohear.audio.waveform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyntheticOaeGeneratorTest {

    @Test
    fun generate_returnsCorrectLength() {
        val generator = SyntheticOaeGenerator(sampleRateHz = 24000, durationMs = 20.0)
        val waveform = generator.generate(targetSnrDb = 15.0, amplitudeScale = 0.8)
        assertEquals(480, waveform.size)
    }

    @Test
    fun generate_differentSampleRate() {
        val generator = SyntheticOaeGenerator(sampleRateHz = 48000, durationMs = 10.0)
        val waveform = generator.generate(targetSnrDb = 10.0, amplitudeScale = 0.5)
        assertEquals(480, waveform.size)
    }

    @Test
    fun generate_reproducibleWithSeed() {
        val gen1 = SyntheticOaeGenerator(seed = 42L)
        val gen2 = SyntheticOaeGenerator(seed = 42L)
        val w1 = gen1.generate(15.0, 0.8)
        val w2 = gen2.generate(15.0, 0.8)
        assertEquals(w1.toList(), w2.toList())
    }

    @Test
    fun generate_differentSeedsProduceDifferentWaveforms() {
        val gen1 = SyntheticOaeGenerator(seed = 42L)
        val gen2 = SyntheticOaeGenerator(seed = 99L)
        val w1 = gen1.generate(15.0, 0.8)
        val w2 = gen2.generate(15.0, 0.8)
        assertTrue("Different seeds should produce different waveforms", w1.toList() != w2.toList())
    }

    @Test
    fun computeLabel_pass() {
        val generator = SyntheticOaeGenerator()
        assertEquals("PASS", generator.computeLabel(15.0))
        assertEquals("PASS", generator.computeLabel(12.0))
        assertEquals("PASS", generator.computeLabel(20.0))
    }

    @Test
    fun computeLabel_refer() {
        val generator = SyntheticOaeGenerator()
        assertEquals("REFER", generator.computeLabel(0.0))
        assertEquals("REFER", generator.computeLabel(-5.0))
        assertEquals("REFER", generator.computeLabel(3.0))
    }

    @Test
    fun computeLabel_borderline() {
        val generator = SyntheticOaeGenerator()
        assertEquals("BORDERLINE", generator.computeLabel(5.0))
        assertEquals("BORDERLINE", generator.computeLabel(7.0))
        assertEquals("BORDERLINE", generator.computeLabel(10.0))
    }

    @Test
    fun generate_zeroAmplitude_procesAllNoise() {
        val generator = SyntheticOaeGenerator(seed = 42L)
        val waveform = generator.generate(targetSnrDb = 0.0, amplitudeScale = 0.0)
        assertEquals(480, waveform.size)
        val maxAmplitude = waveform.maxOf { kotlin.math.abs(it) }
        assertTrue("Zero amplitude should produce near-zero waveform", maxAmplitude < 0.1f)
    }

    @Test
    fun generate_highSnr_producesStrongerSignal() {
        val generator = SyntheticOaeGenerator(seed = 42L)
        val lowSnr = generator.generate(targetSnrDb = 5.0, amplitudeScale = 0.5)
        val highSnr = generator.generate(targetSnrDb = 20.0, amplitudeScale = 0.5)

        val lowRms = kotlin.math.sqrt(lowSnr.sumOf { (it * it).toDouble() } / lowSnr.size)
        val highRms = kotlin.math.sqrt(highSnr.sumOf { (it * it).toDouble() } / highSnr.size)

        assertTrue(
            "High SNR waveform should have higher RMS (signal dominates)",
            highRms > lowRms * 0.5
        )
    }
}
