package com.neohear.audio.pipeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseCaptureTest {

    @Test
    fun replayCapture_notCapturingInitially() {
        val capture = ReplayResponseCapture(
            waveform = floatArrayOf(1f, 2f, 3f),
            sampleRateHz = 24000,
            numRepetitions = 5
        )
        assertFalse(capture.isCapturing())
        assertNull(capture.captureRepetition())
    }

    @Test
    fun replayCapture_capturesSpecifiedRepetitions() {
        val capture = ReplayResponseCapture(
            waveform = floatArrayOf(1f, 2f, 3f),
            sampleRateHz = 24000,
            numRepetitions = 3
        )
        capture.startCapture()
        assertTrue(capture.isCapturing())

        val rep1 = capture.captureRepetition()
        assertNotNull(rep1)
        assertEquals(3, rep1!!.samples.size)
        assertEquals(24000, rep1.sampleRateHz)

        val rep2 = capture.captureRepetition()
        assertNotNull(rep2)

        val rep3 = capture.captureRepetition()
        assertNotNull(rep3)

        // Should be exhausted
        assertNull(capture.captureRepetition())
    }

    @Test
    fun replayCapture_noNoise_exactCopy() {
        val waveform = floatArrayOf(0.1f, 0.2f, 0.3f)
        val capture = ReplayResponseCapture(
            waveform = waveform,
            sampleRateHz = 24000,
            numRepetitions = 1,
            noiseStdDev = 0.0f
        )
        capture.startCapture()
        val rep = capture.captureRepetition()!!
        assertEquals(waveform.toList(), rep.samples.toList())
    }

    @Test
    fun replayCapture_withNoise_addsVariability() {
        val waveform = FloatArray(100) { 0.5f }
        val capture = ReplayResponseCapture(
            waveform = waveform,
            sampleRateHz = 24000,
            numRepetitions = 10,
            noiseStdDev = 0.1f,
            seed = 42L
        )
        capture.startCapture()

        val reps = (0 until 10).map { capture.captureRepetition()!!.samples }

        // All reps should be different from each other (with high probability)
        val allSame = reps.all { it.contentEquals(reps[0]) }
        assertFalse("Repetitions with noise should differ", allSame)

        // All should be roughly around 0.5
        for (rep in reps) {
            val avg = rep.average()
            assertTrue("Average should be near 0.5 ± 0.5", avg in -0.5..1.5)
        }
    }

    @Test
    fun replayCapture_stopPreventsCapture() {
        val capture = ReplayResponseCapture(
            waveform = floatArrayOf(1f),
            sampleRateHz = 24000,
            numRepetitions = 5
        )
        capture.startCapture()
        assertNotNull(capture.captureRepetition())

        capture.stopCapture()
        assertFalse(capture.isCapturing())
        assertNull(capture.captureRepetition())
    }

    @Test
    fun replayCapture_resetAllowsRecapture() {
        val capture = ReplayResponseCapture(
            waveform = floatArrayOf(1f, 2f),
            sampleRateHz = 24000,
            numRepetitions = 2
        )
        capture.startCapture()
        assertNotNull(capture.captureRepetition())
        assertNotNull(capture.captureRepetition())
        assertNull(capture.captureRepetition())

        capture.reset()
        capture.startCapture()
        assertNotNull(capture.captureRepetition())
        assertNotNull(capture.captureRepetition())
    }

    @Test
    fun replayCapture_zeroRepetitions() {
        val capture = ReplayResponseCapture(
            waveform = floatArrayOf(1f),
            sampleRateHz = 24000,
            numRepetitions = 0
        )
        capture.startCapture()
        assertNull(capture.captureRepetition())
    }

    @Test
    fun capturedRepetition_equality() {
        val rep1 = CapturedRepetition(floatArrayOf(1f, 2f), 24000)
        val rep2 = CapturedRepetition(floatArrayOf(1f, 2f), 24000)
        val rep3 = CapturedRepetition(floatArrayOf(1f, 3f), 24000)

        assertEquals(rep1, rep2)
        assertEquals(rep1.hashCode(), rep2.hashCode())
        assertTrue(rep1 != rep3)
    }
}
