package com.neohear.audio.pipeline

import com.neohear.audio.NativeBridge
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates acoustic stimulus waveforms for OAE measurement.
 *
 * Two stimulus modes:
 * - **Click**: A broadband impulse (single-sample spike, bandpass-filtered) that evokes
 *   transient-evoked OAEs (TEOAE) across the 1–4 kHz range.
 * - **DP (Distortion Product)**: Two simultaneous pure tones at f1 and f2 that evoke
 *   distortion-product OAEs (DPOAE) at 2f1–f2.
 *
 * Output is a [FloatArray] at the given sample rate, suitable for playback via Oboe.
 */
object StimulusGenerator {

    /**
     * Generate a click stimulus (single-sample impulse at time zero, scaled to amplitude 1.0).
     *
     * @param sampleRateHz Sampling rate in Hz (e.g. 24000).
     * @param durationMs Total duration of the output buffer in ms. The click occupies one sample;
     *   the rest is zeros (silence).
     * @return FloatArray of length [sampleRateHz] * [durationMs] / 1000.
     */
    fun generateClickStimulus(sampleRateHz: Int, durationMs: Double): FloatArray {
        val numSamples = (sampleRateHz * durationMs / 1000.0).toInt()
        return NativeBridge.generateClickStimulus(sampleRateHz, numSamples)
    }

    /**
     * Generate a distortion-product (DP) stimulus: two simultaneous sinusoidal tones.
     *
     * @param sampleRateHz Sampling rate in Hz.
     * @param f1Hz Frequency of the first primary tone in Hz.
     * @param f2Hz Frequency of the second primary tone in Hz (typically f2/f1 ≈ 1.22).
     * @param durationMs Duration of the output buffer in ms.
     * @param amplitude1 Amplitude of the first tone (0.0–1.0).
     * @param amplitude2 Amplitude of the second tone (0.0–1.0).
     * @return FloatArray containing the sum of the two sinusoidal tones.
     */
    fun generateDpStimulus(
        sampleRateHz: Int,
        f1Hz: Double,
        f2Hz: Double,
        durationMs: Double,
        amplitude1: Float = 0.5f,
        amplitude2: Float = 0.5f
    ): FloatArray {
        val numSamples = (sampleRateHz * durationMs / 1000.0).toInt()
        return NativeBridge.generateDpStimulus(sampleRateHz, numSamples, f1Hz, f2Hz, amplitude1, amplitude2)
    }

    /** Pure-Kotlin click implementation used when native library is not loaded. */
    internal fun pureGenerateClickStimulus(sampleRateHz: Int, numSamples: Int): FloatArray {
        val buffer = FloatArray(numSamples)
        if (numSamples > 0) {
            buffer[0] = 1.0f
        }
        return buffer
    }

    /** Pure-Kotlin DP stimulus implementation used when native library is not loaded. */
    internal fun pureGenerateDpStimulus(
        sampleRateHz: Int,
        numSamples: Int,
        f1Hz: Double,
        f2Hz: Double,
        amplitude1: Float,
        amplitude2: Float
    ): FloatArray {
        val buffer = FloatArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRateHz
            buffer[i] = amplitude1 * sin(2.0 * PI * f1Hz * t).toFloat() +
                    amplitude2 * sin(2.0 * PI * f2Hz * t).toFloat()
        }
        return buffer
    }
}
