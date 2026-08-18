package com.neohear.audio.pipeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AmbientNoiseCheckerTest {

    @Test
    fun checkNoiseLevel_silence_passes() {
        val sample = FloatArray(480) { 0.0f }
        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)
        assertTrue("Silence should pass noise gate", result.ok)
        assertEquals(-120.0, result.noiseLevelDbSPL, 1.0)
    }

    @Test
    fun checkNoiseLevel_veryQuiet_passes() {
        val sample = FloatArray(480) { 0.0001f }
        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)
        assertTrue("Very quiet noise should pass", result.ok)
        assertTrue("Noise level should be < -20 dB", result.noiseLevelDbSPL < -20.0)
    }

    @Test
    fun checkNoiseLevel_loud_fails() {
        val sample = FloatArray(480) { 0.5f }
        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)
        assertFalse("Loud noise should fail gate", result.ok)
        assertTrue("Loud noise level should be > -20 dB", result.noiseLevelDbSPL > -20.0)
    }

    @Test
    fun checkNoiseLevel_customThreshold() {
        val sample = FloatArray(480) { 0.01f }

        // Very strict threshold: should fail
        val strict = AmbientNoiseChecker.checkNoiseLevel(
            sample, 24000,
            thresholds = AmbientNoiseChecker.Thresholds(maxNoiseLevelDbSPL = -60.0)
        )
        assertFalse("Should fail strict threshold", strict.ok)

        // Very lenient threshold: should pass
        val lenient = AmbientNoiseChecker.checkNoiseLevel(
            sample, 24000,
            thresholds = AmbientNoiseChecker.Thresholds(maxNoiseLevelDbSPL = 0.0)
        )
        assertTrue("Should pass lenient threshold", lenient.ok)
    }

    @Test
    fun checkNoiseLevel_dBScale() {
        // RMS of 0.01 → dB = 20 * log10(0.01) = -40 dB
        val sample = FloatArray(480) { 0.01f }
        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)
        assertEquals(-40.0, result.noiseLevelDbSPL, 0.5)
    }

    @Test
    fun checkNoiseLevel_emptySample() {
        val sample = floatArrayOf()
        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)
        assertTrue("Empty sample should pass (0 RMS = -120 dB)", result.ok)
    }

    @Test
    fun ambientNoiseResult_hasAllFields() {
        val sample = FloatArray(480) { 0.01f }
        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)
        assertTrue(result.noiseLevelDbSPL.isFinite())
        assertTrue(result.noiseLevelDbSPL < 0.0)
    }
}
