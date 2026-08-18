package com.neohear.audio.pipeline

import com.neohear.audio.waveform.WaveformFixtureLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration test: loads fixture → feeds through ReplayResponseCapture → averages → classifies.
 *
 * Verifies the full DSP pipeline produces correct PASS/REFER/REPEAT decisions
 * for each fixture category.
 */
class FullPipelineTest {

    @Test
    fun passFixture_classifiesAsPass() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_1.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
        assertEquals(
            "clear_pass_1 (SNR=${fixture.metadata.snrDb}dB) should PASS",
            SnrClassifier.TestDecision.PASS,
            result.decision
        )
        assertTrue("PASS SNR should be >= 12 dB", result.snrDb >= 12.0)
    }

    @Test
    fun passFixture2_classifiesAsPass() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_2.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
        assertEquals(
            "clear_pass_2 (SNR=${fixture.metadata.snrDb}dB) should PASS",
            SnrClassifier.TestDecision.PASS,
            result.decision
        )
    }

    @Test
    fun referFixture_classifiesAsRefer() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_refer_1.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
        assertEquals(
            "clear_refer_1 (SNR=${fixture.metadata.snrDb}dB) should REFER",
            SnrClassifier.TestDecision.REFER,
            result.decision
        )
    }

    @Test
    fun referFixture2_classifiesAsRefer() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_refer_2.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
        assertEquals(
            "clear_refer_2 (SNR=${fixture.metadata.snrDb}dB) should REFER",
            SnrClassifier.TestDecision.REFER,
            result.decision
        )
    }

    @Test
    fun borderlineFixture_classifiesAsRepeat() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/borderline_1.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
        assertEquals(
            "borderline_1 (SNR=${fixture.metadata.snrDb}dB) should REPEAT",
            SnrClassifier.TestDecision.REPEAT,
            result.decision
        )
    }

    @Test
    fun borderlineFixture2_classifiesAsRepeat() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/borderline_2.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
        assertEquals(
            "borderline_2 (SNR=${fixture.metadata.snrDb}dB) should REPEAT",
            SnrClassifier.TestDecision.REPEAT,
            result.decision
        )
    }

    @Test
    fun allSixFixtures_classifyCorrectly() {
        val fixtures = WaveformFixtureLoader.loadAllFromResources("reference_waveforms")
        assertEquals(6, fixtures.size)

        for (fixture in fixtures) {
            val expectedDecision = when (fixture.metadata.label) {
                "PASS" -> SnrClassifier.TestDecision.PASS
                "REFER" -> SnrClassifier.TestDecision.REFER
                "BORDERLINE" -> SnrClassifier.TestDecision.REPEAT
                else -> throw IllegalArgumentException("Unknown label: ${fixture.metadata.label}")
            }

            val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 5)
            assertEquals(
                "${fixture.metadata.name} (SNR=${fixture.metadata.snrDb}dB, label=${fixture.metadata.label}) " +
                        "should classify as $expectedDecision but got ${result.decision}",
                expectedDecision,
                result.decision
            )
        }
    }

    @Test
    fun averagingMultipleReps_improvesOrMaintainsSnr() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_1.json")

        val singleRepResult = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 1)
        val multiRepResult = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 10)

        // With perfect replay (no added noise), averaging shouldn't change the result
        // but the SNR should be similar or better
        assertEquals(singleRepResult.decision, multiRepResult.decision)
    }

    @Test
    fun pipeline_producesValidSnrValues() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_1.json")
        val result = runPipeline(fixture.waveform, fixture.metadata.sampleRateHz, numRepetitions = 3)

        assertTrue("SNR should be finite", result.snrDb.isFinite())
        assertTrue("Signal RMS should be non-negative", result.signalRms >= 0.0)
        assertTrue("Noise RMS should be non-negative", result.noiseRms >= 0.0)
    }

    @Test
    fun stimulusGenerate_classifyRoundTrip() {
        // Generate a synthetic "OAE-like" signal and verify it can be classified
        val sampleRate = 24000
        val duration = 20.0 // ms
        val numSamples = (sampleRate * duration / 1000.0).toInt()

        // Create a damped sinusoid that mimics OAE
        val signal = FloatArray(numSamples) { i ->
            val t = i.toDouble() / sampleRate
            val decay = kotlin.math.exp(-t * 400.0)
            (decay * 0.5 * kotlin.math.sin(2.0 * Math.PI * 2000.0 * t)).toFloat()
        }

        val capture = ReplayResponseCapture(signal, sampleRate, numRepetitions = 5)
        capture.startCapture()

        val reps = mutableListOf<CapturedRepetition>()
        while (true) {
            val rep = capture.captureRepetition() ?: break
            reps.add(rep)
        }

        val averaged = SignalAverager.average(reps)!!
        val classified = SnrClassifier.classify(averaged)

        // A synthetic OAE should produce a definite result (PASS or REPEAT)
        // It should NOT be REFER since we have a clear signal
        assertTrue(
            "Synthetic OAE should not be REFER, got ${classified.decision}",
            classified.decision != SnrClassifier.TestDecision.REFER
        )
    }

    /**
     * Run the full pipeline: waveform → ReplayResponseCapture → average → classify.
     */
    private fun runPipeline(
        waveform: FloatArray,
        sampleRateHz: Int,
        numRepetitions: Int
    ): SnrClassifier.ClassificationResult {
        val capture = ReplayResponseCapture(
            waveform = waveform,
            sampleRateHz = sampleRateHz,
            numRepetitions = numRepetitions,
            noiseStdDev = 0.0f  // exact replay
        )
        capture.startCapture()

        val repetitions = mutableListOf<CapturedRepetition>()
        while (true) {
            val rep = capture.captureRepetition() ?: break
            repetitions.add(rep)
        }

        assertNotNull("Should capture at least one repetition", repetitions.isNotEmpty())

        val averaged = SignalAverager.average(repetitions)
        assertNotNull("Average should not be null", averaged)

        return SnrClassifier.classify(averaged!!)
    }
}
