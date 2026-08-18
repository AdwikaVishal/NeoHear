package com.neohear.audio.pipeline

/**
 * Result of capturing a single repetition of the acoustic response.
 */
data class CapturedRepetition(
    /** The time-domain waveform samples. */
    val samples: FloatArray,
    /** Sample rate in Hz. */
    val sampleRateHz: Int,
    /** Timestamp of capture onset (ms since epoch), or -1 for replay mode. */
    val timestampMs: Long = -1L
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CapturedRepetition) return false
        return samples.contentEquals(other.samples) && sampleRateHz == other.sampleRateHz
    }

    override fun hashCode(): Int = 31 * samples.contentHashCode() + sampleRateHz
}

/**
 * Interface for capturing microphone responses synced to stimulus playback.
 *
 * Implementations provide the N repetitions that [SignalAverager] will average.
 */
interface ResponseCapture {
    /** Start capturing (for live mode, opens the audio stream). */
    fun startCapture()

    /** Stop capturing and release resources. */
    fun stopCapture()

    /**
     * Capture a single repetition of the response.
     *
     * @return A [CapturedRepetition] containing the captured samples, or null if capture failed.
     */
    fun captureRepetition(): CapturedRepetition?

    /**
     * Check if capture is currently active.
     */
    fun isCapturing(): Boolean
}

// Backwards compatibility: make existing replay class easily implement the new
// capture abstraction by providing a small adapter interface type alias.
// Implementations should prefer OaeCaptureSource where possible.


/**
 * Replay-mode capture that feeds pre-recorded waveforms instead of live mic input.
 *
 * This allows the full DSP pipeline to be tested without hardware. Each call to
 * [captureRepetition] returns the next repetition from the provided list. Repetitions
 * can optionally have independent noise added to simulate realistic variability.
 *
 * @param waveform The recorded response waveform to replay.
 * @param sampleRateHz Sample rate of the waveform.
 * @param numRepetitions How many repetitions to yield (each call to [captureRepetition]
 *   returns one repetition).
 * @param noiseStdDev Standard deviation of Gaussian noise added to each repetition.
 *   Set to 0.0 for exact replay (no noise). Non-zero values simulate real capture noise.
 * @param seed Random seed for reproducible noise generation.
 */
class ReplayResponseCapture(
    private val waveform: FloatArray,
    private val sampleRateHz: Int,
    private val numRepetitions: Int = 1,
    private val noiseStdDev: Float = 0.0f,
    private val seed: Long = 42L
) : ResponseCapture, com.neohear.audio.capture.OaeCaptureSource {

    private var repetitionIndex = 0
    private var capturing = false
    private val rng = java.util.Random(seed)

    override fun startCapture() {
        repetitionIndex = 0
        capturing = true
    }

    override fun stopCapture() {
        capturing = false
    }

    override fun captureRepetition(): CapturedRepetition? {
        if (!capturing || repetitionIndex >= numRepetitions) return null

        val samples = if (noiseStdDev > 0f) {
            FloatArray(waveform.size) { i ->
                waveform[i] + rng.nextGaussian().toFloat() * noiseStdDev
            }
        } else {
            waveform.copyOf()
        }

        repetitionIndex++
        return CapturedRepetition(samples = samples, sampleRateHz = sampleRateHz)
    }

    override fun isCapturing(): Boolean = capturing

    override fun isAvailable(): Boolean = true

    /** Reset the capture to yield repetitions from the beginning. */
    fun reset() {
        repetitionIndex = 0
    }
}
