package com.neohear.audio.waveform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WaveformFixtureLoaderTest {

    @Test
    fun loadPassFixture() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_1.json")
        assertNotNull(fixture)
        assertEquals("clear_pass_1", fixture.metadata.name)
        assertEquals("PASS", fixture.metadata.label)
        assertEquals("SYNTHETIC", fixture.metadata.source)
        assertEquals(24000, fixture.metadata.sampleRateHz)
        assertEquals(20.0, fixture.metadata.durationMs, 0.1)
        assertTrue(fixture.metadata.snrDb >= 12.0)
        assertEquals(480, fixture.waveform.size)
    }

    @Test
    fun loadReferFixture() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_refer_1.json")
        assertNotNull(fixture)
        assertEquals("REFER", fixture.metadata.label)
        assertTrue(fixture.metadata.snrDb <= 3.0)
    }

    @Test
    fun loadBorderlineFixture() {
        val fixture = WaveformFixtureLoader.loadFromResources("reference_waveforms/borderline_1.json")
        assertNotNull(fixture)
        assertEquals("BORDERLINE", fixture.metadata.label)
        assertTrue(fixture.metadata.snrDb > 3.0)
        assertTrue(fixture.metadata.snrDb < 12.0)
    }

    @Test
    fun loadAllFixtures() {
        val fixtures = WaveformFixtureLoader.loadAllFromResources("reference_waveforms")
        assertEquals(6, fixtures.size)

        val labels = fixtures.map { it.metadata.label }.toSet()
        assertTrue("Should have PASS fixtures", "PASS" in labels)
        assertTrue("Should have REFER fixtures", "REFER" in labels)
        assertTrue("Should have BORDERLINE fixtures", "BORDERLINE" in labels)
    }

    @Test
    fun allFixturesHaveWaveformData() {
        val fixtures = WaveformFixtureLoader.loadAllFromResources("reference_waveforms")
        for (fixture in fixtures) {
            assertTrue(
                "Fixture ${fixture.metadata.name} should have non-empty waveform",
                fixture.waveform.isNotEmpty()
            )
            assertEquals(
                "Fixture ${fixture.metadata.name} should have 480 samples (20ms at 24kHz)",
                480,
                fixture.waveform.size
            )
        }
    }

    @Test
    fun loadFromJsonString() {
        val json = """
            {
                "name": "test_fixture",
                "label": "PASS",
                "source": "SYNTHETIC",
                "description": "Test",
                "sampleRateHz": 24000,
                "durationMs": 20.0,
                "snrDb": 20.0,
                "frequenciesHz": [1000, 2000],
                "waveform": [0.001, -0.002, 0.003]
            }
        """.trimIndent()

        val fixture = WaveformFixtureLoader.loadFromJson(json)
        assertEquals("test_fixture", fixture.metadata.name)
        assertEquals(3, fixture.waveform.size)
        assertEquals(0.001f, fixture.waveform[0], 0.0001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadNonExistentResource_throws() {
        WaveformFixtureLoader.loadFromResources("reference_waveforms/nonexistent.json")
    }
}
