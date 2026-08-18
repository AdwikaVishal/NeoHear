package com.neohear.audio.waveform

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Loads waveform fixture files from JSON.
 *
 * Fixtures are JSON files containing both metadata and the waveform float array.
 * Each fixture file is a single JSON object with fields: name, label, source, description,
 * sampleRateHz, durationMs, snrDb, frequenciesHz, waveform.
 *
 * **All SYNTHETIC fixtures are clearly labeled "SYNTHETIC" in their source field.**
 *
 * Usage:
 * - From test resources: WaveformFixtureLoader.loadFromResources("reference_waveforms/clear_pass_1.json")
 * - From assets: WaveformFixtureLoader.loadFromAssets(context, "reference_waveforms/clear_pass_1.json")
 * - Load all: WaveformFixtureLoader.loadAllFromResources("reference_waveflows")
 */
object WaveformFixtureLoader {

    private val gson = Gson()

    /** Known fixture names for discovery when directory listing isn't available. */
    val KNOWN_FIXTURES = listOf(
        "clear_pass_1",
        "clear_pass_2",
        "clear_refer_1",
        "clear_refer_2",
        "borderline_1",
        "borderline_2"
    )

    /**
     * Load a single fixture from a JSON string.
     */
    fun loadFromJson(json: String): WaveformFixture {
        val type = object : TypeToken<FixtureJson>() {}.type
        val fixtureJson: FixtureJson = gson.fromJson(json, type)

        val waveform = FloatArray(fixtureJson.waveform.size) {
            fixtureJson.waveform[it].toFloat()
        }

        return WaveformFixture(
            metadata = WaveformMetadata(
                name = fixtureJson.name,
                label = fixtureJson.label,
                source = fixtureJson.source,
                description = fixtureJson.description,
                sampleRateHz = fixtureJson.sampleRateHz,
                durationMs = fixtureJson.durationMs,
                snrDb = fixtureJson.snrDb,
                frequenciesHz = fixtureJson.frequenciesHz
            ),
            waveform = waveform
        )
    }

    /**
     * Load a fixture from a classpath resource file.
     *
     * @param resourcePath Path relative to classpath root, e.g. "reference_waveforms/clear_pass_1.json"
     */
    fun loadFromResources(resourcePath: String): WaveformFixture {
        val json = this::class.java.classLoader.getResource(resourcePath)
            ?.readText()
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
        return loadFromJson(json)
    }

    /**
     * Load a fixture from Android assets.
     *
     * @param context Android Context for accessing assets.
     * @param assetPath Path within assets, e.g. "reference_waveforms/clear_pass_1.json"
     */
    fun loadFromAssets(context: android.content.Context, assetPath: String): WaveformFixture {
        val json = context.assets.open(assetPath).bufferedReader().readText()
        return loadFromJson(json)
    }

    /**
     * Load all fixture JSON files from a classpath resource directory.
     *
     * Uses [KNOWN_FIXTURES] to discover files when directory listing is not available
     * (e.g. inside JARs).
     *
     * @param directoryPath Directory path relative to classpath root, e.g. "reference_waveforms"
     * @return List of loaded fixtures, sorted by name.
     */
    fun loadAllFromResources(directoryPath: String): List<WaveformFixture> {
        val classLoader = this::class.java.classLoader
        val resource = classLoader.getResource(directoryPath)

        if (resource != null && resource.protocol == "file") {
            val files = File(resource.toURI()).listFiles()
            if (files != null) {
                return files
                    .filter { it.extension == "json" }
                    .sortedBy { it.name }
                    .map { loadFromResources("$directoryPath/${it.name}") }
            }
        }

        return KNOWN_FIXTURES.mapNotNull { name ->
            try {
                loadFromResources("$directoryPath/$name.json")
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }

    private class FixtureJson(
        val name: String,
        val label: String,
        val source: String,
        val description: String,
        val sampleRateHz: Int,
        val durationMs: Double,
        val snrDb: Double,
        val frequenciesHz: List<Double>,
        val waveform: List<Double>
    )
}
