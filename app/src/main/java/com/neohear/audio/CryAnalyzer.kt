package com.neohear.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CryAnalysisResult(
    val avgPitchHz: Float,
    val pitchStdDev: Float,
    val avgEnergyDb: Float,
    val jitter: Float,
    val shimmer: Float,
    val voicingRatio: Float,
    val riskFlags: Int
)

object CryAnalyzer {

    private const val SAMPLE_RATE = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val RECORD_DURATION_MS = 5000

    suspend fun analyze(): CryAnalysisResult? = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        if (bufferSize <= 0) return@withContext null

        val audioRecord = try {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize * 4
            )
        } catch (_: SecurityException) {
            return@withContext null
        }

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            return@withContext null
        }

        audioRecord.startRecording()
        val totalSamples = SAMPLE_RATE * RECORD_DURATION_MS / 1000
        val shortBuffer = ShortArray(totalSamples)
        var readSamples = 0

        while (readSamples < totalSamples) {
            val toRead = minOf(bufferSize, totalSamples - readSamples)
            val read = audioRecord.read(shortBuffer, readSamples, toRead)
            if (read > 0) readSamples += read else break
        }

        audioRecord.stop()
        audioRecord.release()

        if (readSamples < SAMPLE_RATE / 2) return@withContext null

        val floatBuffer = FloatArray(readSamples)
        for (i in 0 until readSamples) {
            floatBuffer[i] = shortBuffer[i] / 32768.0f
        }

        val features = NativeBridge.analyzeCry(floatBuffer, SAMPLE_RATE)
        if (features.size < 7) return@withContext null

        CryAnalysisResult(
            avgPitchHz = features[0],
            pitchStdDev = features[1],
            avgEnergyDb = features[2],
            jitter = features[3],
            shimmer = features[4],
            voicingRatio = features[5],
            riskFlags = features[6].toInt()
        )
    }
}
