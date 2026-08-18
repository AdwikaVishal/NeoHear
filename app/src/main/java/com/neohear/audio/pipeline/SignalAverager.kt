package com.neohear.audio.pipeline

import com.neohear.audio.NativeBridge

/**
 * Averages N repetitions of a captured response to reduce noise.
 *
 * Standard synchronous averaging: all repetitions are summed element-wise, then divided
 * by the number of repetitions. Random noise cancels out (improves by √N), while the
 * coherent OAE signal reinforces.
 *
 * The input repetitions must all have the same length. If they don't, the shortest
 * length is used and extra samples are ignored.
 */
object SignalAverager {

    /**
     * Average a list of captured repetitions into a single waveform.
     *
     * @param repetitions List of captured repetitions (all must have the same [CapturedRepetition.sampleRateHz]).
     * @return The averaged waveform as a [FloatArray], or null if the list is empty.
     */
    fun average(repetitions: List<CapturedRepetition>): FloatArray? {
        if (repetitions.isEmpty()) return null
        if (repetitions.size == 1) return repetitions[0].samples.copyOf()

        val buffers = repetitions.map { it.samples }.toTypedArray()
        return NativeBridge.averageBuffers(buffers)
    }

    /** Pure-Kotlin averaging implementation used when native library is not loaded. */
    internal fun pureAverage(buffers: Array<FloatArray>): FloatArray {
        if (buffers.isEmpty()) return floatArrayOf()
        if (buffers.size == 1) return buffers[0].copyOf()

        val minLen = buffers.minOf { it.size }
        val result = FloatArray(minLen)

        for (i in 0 until minLen) {
            var sum = 0.0f
            for (buf in buffers) {
                sum += buf[i]
            }
            result[i] = sum / buffers.size
        }

        return result
    }
}
