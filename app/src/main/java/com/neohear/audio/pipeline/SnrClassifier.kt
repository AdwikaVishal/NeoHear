package com.neohear.audio.pipeline

import com.neohear.audio.NativeBridge
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Classifies an averaged OAE response based on its signal-to-noise ratio.
 *
 * **NOT CLINICALLY CALIBRATED — HACKATHON STAGE ONLY.**
 * These thresholds are placeholder values for development and demo purposes.
 * They have NOT been validated against clinical data and must NOT be used for
 * real diagnostic decisions. A production classifier would require:
 *   - Calibration against a gold-standard audiometric reference
 *   - Frequency-specific SNR analysis (not just broadband RMS)
 *   - Proper statistical validation with sensitivity/specificity analysis
 *   - Regulatory clearance (FDA, CE marking, etc.)
 *
 * The classifier computes a broadband SNR from the averaged waveform and applies
 * fixed thresholds to produce one of three outcomes:
 * - **PASS**: OAE response is clearly present and above noise floor.
 * - **REFER**: OAE response is absent or below threshold — refer for further testing.
 * - **REPEAT**: Result is marginal / uncertain — re-run the test.
 */
object SnrClassifier {

    /**
     * Classification result containing the decision and the measured SNR.
     */
    data class ClassificationResult(
        val decision: TestDecision,
        val snrDb: Double,
        val signalRms: Double,
        val noiseRms: Double
    )

    enum class TestDecision {
        PASS,
        REFER,
        REPEAT
    }

    /**
     * Configuration for classification thresholds.
     *
     * **PLACEHOLDER VALUES — NOT CLINICALLY CALIBRATED.**
     * These defaults are rough heuristics for hackathon-stage development.
     */
    data class Thresholds(
        /** SNR (dB) at or above which the result is a confident PASS. Default: 12.0 dB. */
        val passThresholdDb: Double = 12.0,
        /** SNR (dB) at or below which the result is a confident REFER. Default: 2.0 dB. */
        val referThresholdDb: Double = 2.0
        // Between referThresholdDb and passThresholdDb → REPEAT
    )

    /**
     * Compute broadband SNR and classify the averaged response.
     *
     * The SNR is estimated by:
     * 1. Computing RMS of the full waveform (signal + noise).
     * 2. Estimating noise floor from the tail of the waveform (last 25%), where the
     *    damped OAE signal has mostly decayed.
     * 3. Computing SNR = 10 * log10((total_power - noise_power) / noise_power).
     *
     * @param averagedWaveform The averaged response from [SignalAverager].
     * @param thresholds Classification thresholds (defaults to placeholder values).
     * @return [ClassificationResult] with the decision and SNR.
     */
    fun classify(averagedWaveform: FloatArray, thresholds: Thresholds = Thresholds()): ClassificationResult {
        val totalRms = NativeBridge.computeRms(averagedWaveform)
        val noiseRms = NativeBridge.computeNoiseFloorRms(averagedWaveform)

        val totalPower = totalRms * totalRms
        val noisePower = noiseRms * noiseRms

        val signalPower = (totalPower - noisePower).coerceAtLeast(1e-20)
        val snrDb = 10.0 * log10(signalPower / noisePower.coerceAtLeast(1e-20))

        val decision = when {
            snrDb >= thresholds.passThresholdDb -> TestDecision.PASS
            snrDb <= thresholds.referThresholdDb -> TestDecision.REFER
            else -> TestDecision.REPEAT
        }

        return ClassificationResult(
            decision = decision,
            snrDb = snrDb,
            signalRms = sqrt(signalPower),
            noiseRms = noiseRms
        )
    }

    /** Pure-Kotlin RMS used when native library is not loaded. */
    internal fun pureComputeRms(data: FloatArray): Double {
        if (data.isEmpty()) return 0.0
        val sumSq = data.sumOf { (it * it).toDouble() }
        return sqrt(sumSq / data.size)
    }

    /**
     * Pure-Kotlin noise floor estimation.
     *
     * Estimates noise from the last 25% of the waveform, where the OAE signal
     * (a sum of damped sinusoids with τ = 2–5 ms) has mostly decayed.
     */
    internal fun pureComputeNoiseFloorRms(data: FloatArray): Double {
        if (data.isEmpty()) return 0.0
        val tailStart = (data.size * 0.75).toInt()
        val tail = data.copyOfRange(tailStart, data.size)
        return pureComputeRms(tail)
    }
}
