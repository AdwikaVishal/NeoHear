package com.neohear.ui.screening

import com.neohear.data.entity.Ear
import com.neohear.data.entity.TestResult
import java.util.Date
import java.util.UUID

/**
 * State machine for the guided hearing screening flow.
 *
 * Each state represents a screen in the flow. Transitions are driven by
 * operator actions and DSP pipeline results.
 *
 * Flow:
 * PatientEntry → DeviceCheck → PreTestCheck → Testing → Result
 *                                         ↑         │
 *                                         └─ REPEAT ┘
 * Result(PASS) → done
 * Result(REFER, stage=1) → Testing(stage=2)
 * Result(REFER, stage=2) → ReferForEvaluation → done
 */
sealed class ScreeningState {

    /** Enter patient details before starting the screening. */
    data class PatientEntry(
        val displayName: String = "",
        val dob: Date? = null,
        val ear: Ear = Ear.L,
        val errors: Map<String, String> = emptyMap()
    ) : ScreeningState()

    /** Check that the probe is connected before proceeding. */
    data class DeviceCheck(
        val patientId: UUID,
        val displayName: String,
        val ear: Ear,
        val probeConnected: Boolean = false,
        val checking: Boolean = false
    ) : ScreeningState()

    /** Pre-test ambient noise check — blocks until noise level is acceptable. */
    data class PreTestCheck(
        val patientId: UUID,
        val displayName: String,
        val ear: Ear,
        val noiseLevelDb: Double = -120.0,
        val noiseOk: Boolean = false,
        val checking: Boolean = false
    ) : ScreeningState()

    /** DSP test in progress — runs stimulus + capture + average + classify. */
    data class Testing(
        val patientId: UUID,
        val displayName: String,
        val ear: Ear,
        val stage: Int = 1,
        val noiseLevelDb: Double = 0.0,
        val noiseOk: Boolean? = null,
        // Acquisition sub-state for the testing UI. Defaults keep existing tests stable.
        val acquisitionState: AcquisitionState = AcquisitionState.PREPARING,
        val repetitionsCompleted: Int = 0,
        val totalRepetitions: Int = 5,
        // A small read-only snapshot of the most recent repetition samples for visualization.
        val lastRepetitionSamples: FloatArray? = null,
        val averagingProgress: Float = 0.0f,
        val artifactRejections: Int = 0,
        val estimatedSnrDb: Double? = null
    ) : ScreeningState()

    /** Test result — shows outcome and next steps. */
    data class Result(
        val patientId: UUID,
        val displayName: String,
        val ear: Ear,
        val stage: Int,
        val testResult: TestResult,
        val snrDb: Double = 0.0,
        val signalRms: Double? = null,
        val noiseRms: Double? = null,
        val classifierPassThresholdDb: Double? = null,
        val classifierReferThresholdDb: Double? = null,
        val sessionId: UUID? = null,
        val referralId: UUID? = null,
        val isSaving: Boolean = false
    ) : ScreeningState()

    /** Referral created — show referral details and next steps. */
    data class ReferForEvaluation(
        val patientId: UUID,
        val displayName: String,
        val ear: Ear,
        val sessionId: UUID,
        val referralId: UUID
    ) : ScreeningState()
}

/** Substates for acquisition UI shown during a DSP test. */
enum class AcquisitionState {
    PREPARING,
    STIMULUS,
    CAPTURING,
    AVERAGING,
    ANALYZING,
    COMPLETED,
    ERROR
}

/**
 * Data collected during patient entry.
 */
data class PatientEntryData(
    val displayName: String,
    val dob: Date,
    val ear: Ear
)
