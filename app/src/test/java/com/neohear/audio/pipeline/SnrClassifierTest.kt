package com.neohear.audio.pipeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SnrClassifierTest {

    @Test
    fun pureComputeRms_silence() {
        val rms = SnrClassifier.pureComputeRms(floatArrayOf(0f, 0f, 0f))
        assertEquals(0.0, rms, 1e-10)
    }

    @Test
    fun pureComputeRms_singleSample() {
        val rms = SnrClassifier.pureComputeRms(floatArrayOf(3f))
        assertEquals(3.0, rms, 1e-10)
    }

    @Test
    fun pureComputeRms_uniformSignal() {
        // RMS of [2, 2, 2] = sqrt(4) = 2
        val rms = SnrClassifier.pureComputeRms(floatArrayOf(2f, 2f, 2f))
        assertEquals(2.0, rms, 1e-10)
    }

    @Test
    fun pureComputeRms_knownValues() {
        // [1, -1, 1, -1] → sqrt(1) = 1
        val rms = SnrClassifier.pureComputeRms(floatArrayOf(1f, -1f, 1f, -1f))
        assertEquals(1.0, rms, 1e-10)
    }

    @Test
    fun pureComputeRms_emptyArray() {
        val rms = SnrClassifier.pureComputeRms(floatArrayOf())
        assertEquals(0.0, rms, 1e-10)
    }

    @Test
    fun pureComputeNoiseFloorRms_uniformSignal() {
        // For uniform signal, tail = last 25% = same RMS
        val data = FloatArray(100) { 0.5f }
        val noiseRms = SnrClassifier.pureComputeNoiseFloorRms(data)
        assertEquals(0.5, noiseRms, 1e-10)
    }

    @Test
    fun pureComputeNoiseFloorRms_dampedSignal() {
        // Simulate OAE: strong at start, decays to noise at tail
        val data = FloatArray(480)
        for (i in data.indices) {
            val decay = kotlin.math.exp(-i.toDouble() / 60.0).toFloat()
            data[i] = decay * 2.0f + 0.01f  // signal + noise floor
        }
        val noiseRms = SnrClassifier.pureComputeNoiseFloorRms(data)
        // Tail should be mostly the 0.01 noise floor
        assertTrue("Noise floor RMS should be small", noiseRms < 0.1)
        assertTrue("Noise floor RMS should be > 0", noiseRms > 0.0)
    }

    @Test
    fun classify_highSnr_passes() {
        // Create a signal with clear OAE (high SNR)
        val data = FloatArray(480)
        for (i in 0 until 120) {
            data[i] = (2.0 * kotlin.math.sin(2.0 * Math.PI * i / 24.0)).toFloat()
        }
        // Tail is quiet
        for (i in 120 until 480) {
            data[i] = 0.001f
        }

        val result = SnrClassifier.classify(data)
        assertEquals(SnrClassifier.TestDecision.PASS, result.decision)
        assertTrue("SNR should be > 12 dB for PASS", result.snrDb >= 12.0)
    }

    @Test
    fun classify_lowSnr_refers() {
        // Uniform low-amplitude noise (no signal)
        val rng = java.util.Random(42L)
        val data = FloatArray(480) { rng.nextGaussian().toFloat() * 0.01f }

        val result = SnrClassifier.classify(data)
        assertEquals(SnrClassifier.TestDecision.REFER, result.decision)
    }

    @Test
    fun classify_borderline_repeats() {
        // Construct a signal with SNR ≈ 8 dB (between 3 and 12 thresholds)
        // Noise floor (last 25%) = constant 0.005 → noisePower = 0.000025
        // Signal (first 75%) = sine with amplitude 0.02 → signalRms ≈ 0.0141
        // totalPower ≈ 0.00018, noisePower = 0.000025 → SNR ≈ 8.5 dB → REPEAT
        val data = FloatArray(480)
        val signalLen = 360  // first 75%
        for (i in 0 until signalLen) {
            data[i] = (0.02 * kotlin.math.sin(2.0 * Math.PI * i / 24.0)).toFloat()
        }
        for (i in signalLen until 480) {
            data[i] = 0.005f
        }

        val result = SnrClassifier.classify(data)
        // Should be REPEAT (between referThreshold=3dB and passThreshold=12dB)
        assertTrue("SNR should be between 3 and 12 dB for REPEAT, got ${result.snrDb}dB",
            result.snrDb > 3.0 && result.snrDb < 12.0)
        assertEquals(SnrClassifier.TestDecision.REPEAT, result.decision)
    }

    @Test
    fun classify_withFixture_passFixture() {
        val fixture = com.neohear.audio.waveform.WaveformFixtureLoader
            .loadFromResources("reference_waveforms/clear_pass_1.json")
        val result = SnrClassifier.classify(fixture.waveform)
        assertEquals(
            "PASS fixture with SNR ${fixture.metadata.snrDb}dB should classify as PASS",
            SnrClassifier.TestDecision.PASS,
            result.decision
        )
    }

    @Test
    fun classify_withFixture_referFixture() {
        val fixture = com.neohear.audio.waveform.WaveformFixtureLoader
            .loadFromResources("reference_waveforms/clear_refer_1.json")
        val result = SnrClassifier.classify(fixture.waveform)
        assertEquals(
            "REFER fixture with SNR ${fixture.metadata.snrDb}dB should classify as REFER",
            SnrClassifier.TestDecision.REFER,
            result.decision
        )
    }

    @Test
    fun classify_withFixture_borderlineFixture() {
        val fixture = com.neohear.audio.waveform.WaveformFixtureLoader
            .loadFromResources("reference_waveforms/borderline_1.json")
        val result = SnrClassifier.classify(fixture.waveform)
        assertEquals(
            "BORDERLINE fixture with SNR ${fixture.metadata.snrDb}dB should classify as REPEAT",
            SnrClassifier.TestDecision.REPEAT,
            result.decision
        )
    }

    @Test
    fun classify_customThresholds() {
        // Signal with SNR ≈ 10 dB — PASS at default thresholds (12 dB), but REPEAT if passThreshold=15 dB
        val data = FloatArray(480)
        val signalLen = 360
        for (i in 0 until signalLen) {
            data[i] = (0.03 * kotlin.math.sin(2.0 * Math.PI * i / 24.0)).toFloat()
        }
        for (i in signalLen until 480) {
            data[i] = 0.005f
        }

        // Verify it passes at default thresholds
        val defaultResult = SnrClassifier.classify(data)
        assertTrue("SNR should be > 3 dB", defaultResult.snrDb > 3.0)

        // Set pass threshold very high → should REPEAT
        val result = SnrClassifier.classify(
            data,
            thresholds = SnrClassifier.Thresholds(passThresholdDb = 50.0)
        )
        assertEquals(SnrClassifier.TestDecision.REPEAT, result.decision)
    }

    @Test
    fun classify_returnsNonNegativeSnr() {
        val fixture = com.neohear.audio.waveform.WaveformFixtureLoader
            .loadFromResources("reference_waveforms/clear_pass_1.json")
        val result = SnrClassifier.classify(fixture.waveform)
        assertTrue("SNR should be non-negative", result.snrDb >= 0.0)
        assertTrue("Signal RMS should be non-negative", result.signalRms >= 0.0)
        assertTrue("Noise RMS should be non-negative", result.noiseRms >= 0.0)
    }

    @Test
    fun classificationResult_hasAllFields() {
        val data = FloatArray(480) { 0.01f }
        val result = SnrClassifier.classify(data)
        assertTrue(result.decision in SnrClassifier.TestDecision.entries)
        assertTrue(result.snrDb.isFinite())
        assertTrue(result.signalRms >= 0.0)
        assertTrue(result.noiseRms >= 0.0)
    }
}
