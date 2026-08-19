package com.neohear.audio

/**
 * JNI bridge to native C++ DSP routines.
 *
 * Provides performance-critical signal processing functions (RMS, averaging, stimulus
 * generation) via C++ for on-device use, with pure-Kotlin fallbacks for JVM unit tests
 * where the native library is not loaded.
 */
object NativeBridge {

    private val nativeAvailable: Boolean by lazy {
        try {
            System.loadLibrary("neohear")
            true
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }

    // ── JNI declarations ──────────────────────────────────────────────────

    private external fun nativeGenerateClickStimulus(sampleRateHz: Int, numSamples: Int): FloatArray

    private external fun nativeGenerateDpStimulus(
        sampleRateHz: Int,
        numSamples: Int,
        f1Hz: Double,
        f2Hz: Double,
        amplitude1: Float,
        amplitude2: Float
    ): FloatArray

    private external fun nativeAverageBuffers(buffers: Array<FloatArray>): FloatArray

    private external fun nativeComputeRms(data: FloatArray): Double

    private external fun nativeComputeNoiseFloorRms(data: FloatArray): Double

    private external fun nativeAnalyzeCry(audio: FloatArray, sampleRate: Int): FloatArray

    // ── Public API (auto-selects JNI or pure-Kotlin) ──────────────────────

    fun generateClickStimulus(sampleRateHz: Int, numSamples: Int): FloatArray {
        return if (nativeAvailable) {
            nativeGenerateClickStimulus(sampleRateHz, numSamples)
        } else {
            com.neohear.audio.pipeline.StimulusGenerator.pureGenerateClickStimulus(sampleRateHz, numSamples)
        }
    }

    fun generateDpStimulus(
        sampleRateHz: Int,
        numSamples: Int,
        f1Hz: Double,
        f2Hz: Double,
        amplitude1: Float,
        amplitude2: Float
    ): FloatArray {
        return if (nativeAvailable) {
            nativeGenerateDpStimulus(sampleRateHz, numSamples, f1Hz, f2Hz, amplitude1, amplitude2)
        } else {
            com.neohear.audio.pipeline.StimulusGenerator.pureGenerateDpStimulus(
                sampleRateHz, numSamples, f1Hz, f2Hz, amplitude1, amplitude2
            )
        }
    }

    fun averageBuffers(buffers: Array<FloatArray>): FloatArray {
        return if (nativeAvailable) {
            nativeAverageBuffers(buffers)
        } else {
            com.neohear.audio.pipeline.SignalAverager.pureAverage(buffers)
        }
    }

    fun computeRms(data: FloatArray): Double {
        return if (nativeAvailable) {
            nativeComputeRms(data)
        } else {
            com.neohear.audio.pipeline.SnrClassifier.pureComputeRms(data)
        }
    }

    fun computeNoiseFloorRms(data: FloatArray): Double {
        return if (nativeAvailable) {
            nativeComputeNoiseFloorRms(data)
        } else {
            com.neohear.audio.pipeline.SnrClassifier.pureComputeNoiseFloorRms(data)
        }
    }

    /**
     * EXPERIMENTAL — Analyze cry audio and return extracted features.
     *
     * Returns a FloatArray of 7 elements:
     * [avgPitchHz, pitchStdDev, avgEnergyDb, jitter, shimmer, voicingRatio, riskFlags]
     *
     * Falls back to a zero-filled array if native library is unavailable.
     */
    fun analyzeCry(audio: FloatArray, sampleRate: Int): FloatArray {
        return if (nativeAvailable) {
            nativeAnalyzeCry(audio, sampleRate)
        } else {
            FloatArray(7)
        }
    }

    // ── Original stub ─────────────────────────────────────────────────────

    private external fun nativePing(): String

    fun ping(): String {
        return if (nativeAvailable) {
            nativePing()
        } else {
            "Native library not available"
        }
    }
}
