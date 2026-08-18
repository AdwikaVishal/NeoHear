package com.neohear.audio.capture

import com.neohear.audio.NativeBridge
import com.neohear.audio.pipeline.CapturedRepetition
import android.util.Log

/**
 * Placeholder RealOaeCapture that integrates with NativeBridge/JNI in future.
 * Currently reports unavailable and fails gracefully.
 */
class RealOaeCapture : OaeCaptureSource {
    override fun isAvailable(): Boolean {
        // Real capture not implemented — report unavailable
        return false
    }

    override fun startCapture() {
        // No-op; real implementation will call nativeStartCapture via NativeBridge
        Log.w("RealOaeCapture", "Real OAE probe capture is not available in this prototype.")
    }

    override fun stopCapture() {
        // No-op
    }

    override fun captureRepetition(): CapturedRepetition? {
        // Not available — return null to indicate capture failure
        return null
    }

    override fun isCapturing(): Boolean = false
}
