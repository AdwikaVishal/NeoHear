package com.neohear.audio.capture

import com.neohear.audio.pipeline.CapturedRepetition

/**
 * Abstraction for an OAE capture source. Implementations produce captured repetitions
 * compatible with the existing DSP pipeline (CapturedRepetition).
 */
interface OaeCaptureSource {
    /** True if the capture source is available on the current device */
    fun isAvailable(): Boolean

    /** Start capture resources. */
    fun startCapture()

    /** Stop capture and release resources. */
    fun stopCapture()

    /** Capture a single repetition. Returns null when capture failed or not available. */
    fun captureRepetition(): CapturedRepetition?

    /** Check if currently capturing. */
    fun isCapturing(): Boolean
}
