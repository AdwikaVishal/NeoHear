package com.neohear.audio.waveform

/**
 * A complete waveform fixture: metadata + the time-domain float array.
 *
 * All SYNTHETIC fixtures are clearly labeled as such — never misrepresent
 * synthetic data as real clinical recordings.
 */
data class WaveformFixture(
    val metadata: WaveformMetadata,
    val waveform: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaveformFixture) return false
        return metadata == other.metadata && waveform.contentEquals(other.waveform)
    }

    override fun hashCode(): Int {
        return 31 * metadata.hashCode() + waveform.contentHashCode()
    }
}
