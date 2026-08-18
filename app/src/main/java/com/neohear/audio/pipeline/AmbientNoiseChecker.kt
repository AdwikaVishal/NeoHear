package com.neohear.audio.pipeline

import com.neohear.audio.NativeBridge

/**
 * Checks ambient noise level from a short pre-test microphone sample.
 *
 * Before running the OAE test, a brief recording of ambient ear-canal noise is captured.
 * If the noise is too high, the test should not proceed (poor seal, crying baby, etc.).
 *
 * @property noiseLevelDbSPL Estimated noise level in dB SPL (or arbitrary units if
 *   uncalibrated — the absolute value doesn't matter, only the threshold comparison).
 * @property ok True if noise is below the acceptable threshold.
 */
data class AmbientNoiseResult(
    val noiseLevelDbSPL: Double,
    val ok: Boolean
)

/**
 * Pre-test ambient noise gate.
 *
 * **PLACEHOLDER THRESHOLDS — NOT CALIBRATED TO dB SPL.**
 * The threshold values are rough heuristics for hackathon development.
 * A production implementation would need calibration against a reference sound level meter.
 */
object AmbientNoiseChecker {

    /**
     * Configuration for the noise gate.
     *
     * @property maxNoiseLevelDbSPL Maximum acceptable noise level. Default: -20.0 dB (relative).
     *   **NOT CLINICALLY CALIBRATED.**
     */
    data class Thresholds(
        val maxNoiseLevelDbSPL: Double = -20.0
    )

    /**
     * Check the ambient noise level from a pre-test microphone sample.
     *
     * @param sample Short recording of ambient noise (typically 50–200 ms).
     * @param sampleRateHz Sample rate in Hz.
     * @param thresholds Acceptable noise thresholds.
     * @return [AmbientNoiseResult] with the measured noise level and pass/fail flag.
     */
    fun checkNoiseLevel(
        sample: FloatArray,
        sampleRateHz: Int,
        thresholds: Thresholds = Thresholds()
    ): AmbientNoiseResult {
        val rms = NativeBridge.computeRms(sample)
        // Convert RMS to dB (relative to full-scale)
        val noiseLevelDb = if (rms > 0) 20.0 * kotlin.math.log10(rms) else -120.0

        return AmbientNoiseResult(
            noiseLevelDbSPL = noiseLevelDb,
            ok = noiseLevelDb <= thresholds.maxNoiseLevelDbSPL
        )
    }
}
