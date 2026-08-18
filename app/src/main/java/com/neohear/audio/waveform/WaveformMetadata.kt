package com.neohear.audio.waveform

/**
 * Metadata describing a waveform fixture's provenance and ground-truth labeling.
 *
 * @property label Ground-truth classifier label: "PASS", "REFER", or "BORDERLINE".
 * @property source "SYNTHETIC" for generated data, or a citation for real clinical data.
 * @property description Human-readable description of this fixture.
 * @property sampleRateHz Sampling rate in Hz.
 * @property durationMs Duration in milliseconds.
 * @property snrDb Signal-to-noise ratio in dB.
 * @property frequenciesHz Test frequencies present in the signal.
 */
data class WaveformMetadata(
    val name: String,
    val label: String,
    val source: String,
    val description: String,
    val sampleRateHz: Int,
    val durationMs: Double,
    val snrDb: Double,
    val frequenciesHz: List<Double>
)
