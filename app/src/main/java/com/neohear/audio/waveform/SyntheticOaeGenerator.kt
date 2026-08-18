package com.neohear.audio.waveform

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Synthetic TEOAE waveform generator for DSP pipeline development and testing.
 *
 * Produces plausible time-domain acoustic response arrays modeled as:
 * - Sum of damped sinusoids at representative OAE frequencies (1-4 kHz)
 * - Exponential decay envelopes
 * - Additive Gaussian white noise at a configurable floor
 *
 * **SYNTHETIC DATA — NOT FOR CLINICAL USE.**
 * These waveforms are physics-based approximations for algorithm testing only.
 * They should never be presented as real clinical recordings.
 *
 * @param sampleRateHz Sampling rate (default 24000 Hz, typical for OAE equipment).
 * @param durationMs Duration of the recording in ms (default 20 ms).
 * @param frequenciesHz Center frequencies for the damped sinusoid components.
 * @param seed Random seed for reproducible noise generation.
 */
class SyntheticOaeGenerator(
    private val sampleRateHz: Int = 24000,
    private val durationMs: Double = 20.0,
    private val frequenciesHz: List<Double> = listOf(1000.0, 1500.0, 2000.0, 3000.0, 4000.0),
    private val seed: Long = 42L
) {

    private val numSamples: Int = (sampleRateHz * durationMs / 1000.0).toInt()

    /**
     * Generate a synthetic OAE waveform.
     *
     * @param targetSnrDb Desired signal-to-noise ratio in dB. Higher = cleaner signal.
     * @param amplitudeScale Overall amplitude scaling factor (0.0-1.0). Controls response strength.
     *   - PASS: typically 0.5-1.0 (strong OAE response)
     *   - REFER: typically 0.0-0.15 (weak/absent response)
     *   - BORDERLINE: typically 0.15-0.35 (marginal response)
     * @return FloatArray of length [numSamples] representing the time-domain waveform.
     */
    fun generate(targetSnrDb: Double, amplitudeScale: Double): FloatArray {
        val signal = generateSignal(amplitudeScale)
        val noise = generateNoise()

        val signalRms = rms(signal)
        val noiseRms = rms(noise)

        if (noiseRms == 0f || signalRms == 0f) {
            return if (signalRms == 0f) noise else signal
        }

        val currentSnrDb = 20.0 * kotlin.math.log10(signalRms.toDouble() / noiseRms.toDouble())
        val gainDb = targetSnrDb - currentSnrDb
        val gainLinear = (10.0).pow(gainDb / 20.0).toFloat()

        val adjustedNoise = noise.map { it * gainLinear }.toFloatArray()

        return FloatArray(numSamples) { i ->
            signal[i] + adjustedNoise[i]
        }
    }

    /**
     * Compute the ground-truth label for a given SNR.
     * Clinical pass criterion for newborn OAE screening: SNR >= 6 dB at sufficient frequencies.
     */
    fun computeLabel(snrDb: Double): String = when {
        snrDb >= 12.0 -> "PASS"
        snrDb <= 3.0 -> "REFER"
        else -> "BORDERLINE"
    }

    private fun generateSignal(scale: Double): FloatArray {
        val t = FloatArray(numSamples) { it.toFloat() / sampleRateHz }
        val signal = FloatArray(numSamples)
        val rng = kotlin.random.Random(seed)

        for (freq in frequenciesHz) {
            val amplitude = (scale * (0.5 + rng.nextDouble() * 0.5)).toFloat()
            val tau = (0.002 + rng.nextDouble() * 0.003).toFloat()
            val phase = (rng.nextDouble() * 2 * PI).toFloat()

            for (i in 0 until numSamples) {
                val tSec = t[i]
                val envelope = exp(-tSec / tau)
                signal[i] += amplitude * envelope * sin(2.0 * PI * freq * tSec + phase).toFloat()
            }
        }

        return signal
    }

    private fun generateNoise(): FloatArray {
        val rng = java.util.Random(seed + 1000)
        return FloatArray(numSamples) {
            rng.nextGaussian().toFloat() * 0.01f
        }
    }

    private fun rms(data: FloatArray): Float {
        if (data.isEmpty()) return 0f
        val sumSq = data.sumOf { (it * it).toDouble() }
        return sqrt((sumSq / data.size).toFloat())
    }
}
