package com.neohear.audio.pipeline

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalAveragerTest {

    @Test
    fun pureAverage_emptyInput() {
        val result = SignalAverager.pureAverage(arrayOf())
        assertEquals(0, result.size)
    }

    @Test
    fun pureAverage_singleBuffer() {
        val buf = floatArrayOf(1f, 2f, 3f)
        val result = SignalAverager.pureAverage(arrayOf(buf))
        assertArrayEquals(floatArrayOf(1f, 2f, 3f), result, 1e-6f)
    }

    @Test
    fun pureAverage_singleBuffer_copiesOriginal() {
        val buf = floatArrayOf(1f, 2f, 3f)
        val result = SignalAverager.pureAverage(arrayOf(buf))
        buf[0] = 999f
        assertTrue("Should be a copy, not a reference", result[0] == 1f)
    }

    @Test
    fun pureAverage_twoBuffers() {
        val buf1 = floatArrayOf(2f, 4f, 6f)
        val buf2 = floatArrayOf(0f, 2f, 4f)
        val result = SignalAverager.pureAverage(arrayOf(buf1, buf2))
        assertArrayEquals(floatArrayOf(1f, 3f, 5f), result, 1e-6f)
    }

    @Test
    fun pureAverage_threeBuffers() {
        val buf1 = floatArrayOf(3f, 6f)
        val buf2 = floatArrayOf(0f, 3f)
        val buf3 = floatArrayOf(3f, 3f)
        val result = SignalAverager.pureAverage(arrayOf(buf1, buf2, buf3))
        assertArrayEquals(floatArrayOf(2f, 4f), result, 1e-6f)
    }

    @Test
    fun pureAverage_unequalLengths_usesShortest() {
        val buf1 = floatArrayOf(1f, 2f, 3f, 4f, 5f)
        val buf2 = floatArrayOf(10f, 20f)
        val result = SignalAverager.pureAverage(arrayOf(buf1, buf2))
        assertEquals(2, result.size)
        assertArrayEquals(floatArrayOf(5.5f, 11f), result, 1e-6f)
    }

    @Test
    fun average_emptyList_returnsNull() {
        assertNull(SignalAverager.average(emptyList()))
    }

    @Test
    fun average_singleRepetition_returnsCopy() {
        val rep = CapturedRepetition(
            samples = floatArrayOf(1f, 2f, 3f),
            sampleRateHz = 24000
        )
        val result = SignalAverager.average(listOf(rep))
        assertNotNull(result)
        assertArrayEquals(floatArrayOf(1f, 2f, 3f), result!!, 1e-6f)
    }

    @Test
    fun average_multipleRepetitions_averagesCorrectly() {
        val rep1 = CapturedRepetition(floatArrayOf(4f, 8f), 24000)
        val rep2 = CapturedRepetition(floatArrayOf(2f, 4f), 24000)
        val result = SignalAverager.average(listOf(rep1, rep2))
        assertNotNull(result)
        assertArrayEquals(floatArrayOf(3f, 6f), result!!, 1e-6f)
    }

    @Test
    fun average_noisyRepetitions_reducesVariance() {
        val rng = java.util.Random(42L)
        val signal = FloatArray(200) { i ->
            (0.5 * kotlin.math.sin(2.0 * Math.PI * i / 24.0)).toFloat()
        }

        val reps = (0 until 50).map { _ ->
            CapturedRepetition(
                samples = FloatArray(signal.size) { i ->
                    signal[i] + rng.nextGaussian().toFloat() * 0.1f
                },
                sampleRateHz = 24000
            )
        }

        val averaged = SignalAverager.average(reps)!!
        val signalRms = SignalAverager.pureAverage(arrayOf(signal))

        // Averaged should be closer to the true signal than any individual repetition
        val avgDiff = averaged.zip(signalRms).sumOf { ((it.first - it.second) * (it.first - it.second)).toDouble() }
        assertTrue("Averaged waveform should be close to true signal", avgDiff < 0.5)
    }
}
