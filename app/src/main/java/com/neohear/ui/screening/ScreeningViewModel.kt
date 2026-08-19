package com.neohear.ui.screening

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neohear.NeoHearApp
import com.neohear.audio.pipeline.AmbientNoiseChecker
import com.neohear.audio.pipeline.CapturedRepetition
import com.neohear.audio.pipeline.ReplayResponseCapture
import com.neohear.audio.pipeline.SignalAverager
import com.neohear.audio.pipeline.SnrClassifier
import com.neohear.data.dao.PatientDao
import com.neohear.data.dao.ReferralDao
import com.neohear.data.dao.TestSessionDao
import com.neohear.data.entity.Ear
import com.neohear.data.entity.Mode
import com.neohear.data.entity.Patient
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.TestResult
import com.neohear.data.entity.TestSession
import com.neohear.audio.waveform.WaveformFixtureLoader
import com.neohear.demo.fixtureFor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

/**
 * ViewModel for the guided hearing screening flow.
 *
 * Drives the state machine from [ScreeningState.PatientEntry] through
 * device check, pre-test noise check, DSP testing, and result classification.
 *
 * In demo mode, the live mic [ResponseCapture] is replaced with a
 * [ReplayResponseCapture] that replays one of the Prompt 2 fixtures,
 * allowing the full pipeline to run without a physical probe.
 */
class ScreeningViewModel(
    application: Application,
    private val patientDao: PatientDao,
    private val testSessionDao: TestSessionDao,
    private val referralDao: ReferralDao,
    private val dspDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    /** Factory for creating [ScreeningViewModel] with the app's database DAOs. */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = application as? NeoHearApp
            val db = app?.database
            return ScreeningViewModel(
                application,
                db?.patientDao() ?: throw IllegalStateException("AppDatabase not initialized"),
                db.testSessionDao(),
                db.referralDao()
            ) as T
        }
    }

    /** Constructor for tests — accepts DAOs directly without NeoHearApp cast. */
    internal constructor(
        application: Application,
        patientDao: PatientDao,
        testSessionDao: TestSessionDao,
        referralDao: ReferralDao,
        dspDispatcher: CoroutineDispatcher = Dispatchers.Default,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        _testMarker: Unit = Unit
    ) : this(application, patientDao, testSessionDao, referralDao, dspDispatcher, ioDispatcher)

    private val _state = MutableStateFlow<ScreeningState>(ScreeningState.PatientEntry())
    val state: StateFlow<ScreeningState> = _state.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    // Current demo scenario override (not persisted) — when set, runDspPipeline will use fixtures
    private var demoScenarioOverride: com.neohear.demo.DemoScenario? = null
    private val _currentDemoScenario = MutableStateFlow<com.neohear.demo.DemoScenario?>(null)
    val currentDemoScenario = _currentDemoScenario.asStateFlow()

    fun startDemoScenario(scenarioId: String) {
        val scenario = com.neohear.demo.DemoScenarioRegistry.findById(scenarioId) ?: return
        demoScenarioOverride = scenario
        _currentDemoScenario.value = scenario
        com.neohear.demo.DemoScenarioManager.currentScenario.value = scenario
        // Ensure demo mode is enabled
        _isDemoMode.value = true

        // Create a synthetic demo patient and start the flow
        val patient = Patient(
            id = UUID.randomUUID(),
            displayNameOrCode = "Demo Baby ${patientCounter()} (DEMO)",
            dob = Date(0),
            sex = null
        )

        _state.value = ScreeningState.DeviceCheck(
            patientId = patient.id,
            displayName = patient.displayNameOrCode,
            ear = Ear.L,
            probeConnected = true,
            checking = false
        )

        viewModelScope.launch {
            patientDao.insert(patient)

            // Move to pre-test check
            _state.update {
                ScreeningState.PreTestCheck(
                    patientId = patient.id,
                    displayName = patient.displayNameOrCode,
                    ear = Ear.L,
                    noiseLevelDb = if (scenario.noiseMode == com.neohear.demo.DemoNoiseMode.HIGH) 0.0 else -60.0,
                    noiseOk = scenario.noiseMode != com.neohear.demo.DemoNoiseMode.HIGH,
                    checking = false
                )
            }

            // If noise is acceptable, proceed to testing
            if (scenario.noiseMode != com.neohear.demo.DemoNoiseMode.HIGH) {
                // give the UI a short moment
                delay(300)
                _state.update {
                    ScreeningState.Testing(
                        patientId = patient.id,
                        displayName = patient.displayNameOrCode,
                        ear = Ear.L,
                        stage = 1,
                        noiseLevelDb = -60.0,
                        noiseOk = true
                    )
                }
                runDspTest()
            }
        }
    }

    private fun patientCounter(): Int {
        return (System.currentTimeMillis() % 1000).toInt()
    }

    /** Number of repetitions for the DSP pipeline. */
    private val numRepetitions = 5

    // ── Patient Entry ────────────────────────────────────────────────────

    fun updateDisplayName(name: String) {
        val s = _state.value as? ScreeningState.PatientEntry ?: return
        _state.update { s.copy(displayName = name, errors = s.errors - "name") }
    }

    fun updateDob(dob: Date) {
        val s = _state.value as? ScreeningState.PatientEntry ?: return
        _state.update { s.copy(dob = dob, errors = s.errors - "dob") }
    }

    fun updateEar(ear: Ear) {
        val s = _state.value as? ScreeningState.PatientEntry ?: return
        _state.update { s.copy(ear = ear) }
    }

    fun submitPatientEntry() {
        val s = _state.value as? ScreeningState.PatientEntry ?: return
        val errors = mutableMapOf<String, String>()
        if (s.displayName.isBlank()) errors["name"] = "Name is required"
        if (s.dob == null) errors["dob"] = "Date of birth is required"

        if (errors.isNotEmpty()) {
            _state.update { s.copy(errors = errors) }
            return
        }

        val patient = Patient(
            id = UUID.randomUUID(),
            displayNameOrCode = s.displayName.trim(),
            dob = s.dob!!,
            sex = null
        )

        _state.value = ScreeningState.DeviceCheck(
            patientId = patient.id,
            displayName = patient.displayNameOrCode,
            ear = s.ear
        )

        viewModelScope.launch {
            patientDao.insert(patient)
        }
    }

    fun submitPatientDetails(name: String, dob: Date, ear: Ear) {
        val patient = Patient(
            id = UUID.randomUUID(),
            displayNameOrCode = name.trim(),
            dob = dob,
            sex = null
        )

        _state.value = ScreeningState.DeviceCheck(
            patientId = patient.id,
            displayName = patient.displayNameOrCode,
            ear = ear
        )

        viewModelScope.launch {
            patientDao.insert(patient)
        }
    }

    // ── Device Check ─────────────────────────────────────────────────────

    fun simulateProbeCheck() {
        val s = _state.value as? ScreeningState.DeviceCheck ?: return
        _state.value = s.copy(checking = false, probeConnected = true)
    }

    fun proceedFromDeviceCheck() {
        val s = _state.value as? ScreeningState.DeviceCheck ?: return
        if (!s.probeConnected) return
        _state.update {
            ScreeningState.PreTestCheck(
                patientId = s.patientId,
                displayName = s.displayName,
                ear = s.ear
            )
        }
    }

    fun routeToQuestionnaire(): Boolean {
        // Called when operator chooses risk questionnaire instead of probe test
        return true // Navigation handled by caller
    }

    // ── Pre-Test Check ───────────────────────────────────────────────────

    fun runPreTestCheck() {
        val s = _state.value as? ScreeningState.PreTestCheck ?: return
        _state.update { s.copy(checking = true) }

        val sample = if (_isDemoMode.value) {
            FloatArray(4800) { (Math.random().toFloat() - 0.5f) * 0.001f }
        } else {
            FloatArray(4800) { (Math.random().toFloat() - 0.5f) * 0.001f }
        }

        val result = AmbientNoiseChecker.checkNoiseLevel(sample, 24000)

        _state.value = s.copy(
            checking = false,
            noiseLevelDb = result.noiseLevelDbSPL,
            noiseOk = result.ok
        )
    }

    fun proceedFromPreTestCheck() {
        val s = _state.value as? ScreeningState.PreTestCheck ?: return
        if (!s.noiseOk) return
        _state.update {
            ScreeningState.Testing(
                patientId = s.patientId,
                displayName = s.displayName,
                ear = s.ear,
                stage = 1,
                noiseLevelDb = s.noiseLevelDb,
                noiseOk = s.noiseOk
            )
        }
        runDspTest()
    }

    // ── DSP Testing ──────────────────────────────────────────────────────

    private fun runDspTest() {
        val s = _state.value as? ScreeningState.Testing ?: return

        viewModelScope.launch {
            val waveform = withContext(dspDispatcher) {
                runDspPipeline(s.ear, s.stage)
            }

            val classification = SnrClassifier.classify(waveform)
            val testResult = when (classification.decision) {
                SnrClassifier.TestDecision.PASS -> TestResult.PASS
                SnrClassifier.TestDecision.REFER -> TestResult.REFER
                SnrClassifier.TestDecision.REPEAT -> TestResult.REPEAT
            }

            val sessionId = UUID.randomUUID()

            // Capture classifier thresholds used (defaults) for transparency
            val thresholds = SnrClassifier.Thresholds()

            _state.value = ScreeningState.Result(
                patientId = s.patientId,
                displayName = s.displayName,
                ear = s.ear,
                stage = s.stage,
                testResult = testResult,
                snrDb = classification.snrDb,
                signalRms = classification.signalRms,
                noiseRms = classification.noiseRms,
                classifierPassThresholdDb = thresholds.passThresholdDb,
                classifierReferThresholdDb = thresholds.referThresholdDb,
                sessionId = sessionId
            )

            // Fire-and-forget DB write
            withContext(ioDispatcher) {
                val session = TestSession(
                    id = sessionId,
                    patientId = s.patientId,
                    ear = s.ear,
                    stage = s.stage,
                    timestamp = System.currentTimeMillis(),
                    preCheckNoiseLevel = s.noiseLevelDb.toFloat(),
                    preCheckSealOk = true,
                    mode = if (_isDemoMode.value) Mode.DEMO else Mode.PROBE,
                    rawSignalRef = null,
                    snrValue = classification.snrDb.toFloat(),
                    result = testResult
                )
                testSessionDao.insert(session)
                    // Mark this session as pending synchronization (local-only simulation)
                    com.neohear.sync.SyncManager.getInstance(getApplication()).addPending(session.id.toString(), "TestSession", session.mode == Mode.DEMO)
            }
        }
    }

    /**
     * Run the DSP pipeline: generate stimulus → capture repetitions → average → classify.
     *
     * In demo mode, uses [ReplayResponseCapture] with a fixture waveform.
     * In live mode, would use a real mic capture (placeholder for now).
     */
    private suspend fun runDspPipeline(ear: Ear, stage: Int): FloatArray {
        return withContext(dspDispatcher) {
            // Update state: preparing
            _state.update { current ->
                (current as? ScreeningState.Testing)?.copy(acquisitionState = AcquisitionState.PREPARING)
                    ?: current
            }

            // Obtain an OaeCaptureSource via factory. Pass demo scenario fixture if present.
            val demoFixtureName = demoScenarioOverride?.fixtureFor(ear, stage)
            val captureSource = com.neohear.audio.capture.OaeCaptureFactory.create(
                mode = if (_isDemoMode.value) com.neohear.data.entity.Mode.DEMO else com.neohear.data.entity.Mode.PROBE,
                ear = ear,
                stage = stage,
                demoFixtureName = demoFixtureName
            )
            // Present stimulus
            _state.update { current ->
                (current as? ScreeningState.Testing)?.copy(acquisitionState = AcquisitionState.STIMULUS)
                    ?: current
            }

            // Start the capture source
            captureSource.startCapture()
            val repetitions = mutableListOf<CapturedRepetition>()

            repeat(numRepetitions) { idx ->
                // Update UI to capturing (before the repetition)
                _state.update { current ->
                    (current as? ScreeningState.Testing)?.copy(
                        acquisitionState = AcquisitionState.CAPTURING,
                        repetitionsCompleted = idx
                    ) ?: current
                }

                // Actually capture one repetition (may be null on failure)
                val rep = captureSource.captureRepetition()
                rep?.let {
                    repetitions.add(it)
                    // publish a small snapshot for waveform visualization
                    val snapshot = it.samples.copyOfRange(0, kotlin.math.min(it.samples.size, 512))
                    _state.update { current ->
                        (current as? ScreeningState.Testing)?.copy(
                            repetitionsCompleted = idx + 1,
                            lastRepetitionSamples = snapshot
                        ) ?: current
                    }
                }

                delay(200) // simulate real-time capture delay per repetition
            }

            captureSource.stopCapture()

            // Averaging
            _state.update { current ->
                (current as? ScreeningState.Testing)?.copy(acquisitionState = AcquisitionState.AVERAGING)
                    ?: current
            }

            val averaged = SignalAverager.average(repetitions)

            // Analysis
            _state.update { current ->
                (current as? ScreeningState.Testing)?.copy(acquisitionState = AcquisitionState.ANALYZING)
                    ?: current
            }

            averaged ?: FloatArray(0)
        }
    }

    private fun currentAsLimitedSample(rep: CapturedRepetition?): FloatArray? {
        if (rep == null) return null
        return rep.samples.copyOfRange(0, kotlin.math.min(rep.samples.size, 512))
    }

    // ── Result Handling ──────────────────────────────────────────────────

    fun handleResult() {
        val s = _state.value as? ScreeningState.Result ?: return

        when (s.testResult) {
            TestResult.PASS -> {
                // Done — show pass confirmation
            }
            TestResult.REFER -> {
                if (s.stage < 2) {
                    // Auto-transition to Stage 2 (repeat)
                    _state.update {
                        ScreeningState.Testing(
                            patientId = s.patientId,
                            displayName = s.displayName,
                            ear = s.ear,
                            stage = 2,
                            noiseLevelDb = 0.0
                        )
                    }
                    runDspTest()
                } else {
                    // Stage 2 REFER — create referral
                    createReferral(s)
                }
            }
            TestResult.REPEAT -> {
                // Prompt retry — go back to PreTestCheck
                _state.update {
                    ScreeningState.PreTestCheck(
                        patientId = s.patientId,
                        displayName = s.displayName,
                        ear = s.ear
                    )
                }
            }
        }
    }

    private fun createReferral(result: ScreeningState.Result) {
        val referralId = UUID.randomUUID()

        _state.value = ScreeningState.ReferForEvaluation(
            patientId = result.patientId,
            displayName = result.displayName,
            ear = result.ear,
            sessionId = result.sessionId ?: UUID.randomUUID(),
            referralId = referralId
        )

        viewModelScope.launch {
            val referral = Referral(
                id = referralId,
                patientId = result.patientId,
                testSessionId = result.sessionId ?: UUID.randomUUID(),
                status = ReferralStatus.PENDING
            )
            referralDao.insert(referral)
            // Mark referral pending sync
            com.neohear.sync.SyncManager.getInstance(getApplication()).addPending(referral.id.toString(), "Referral", false)
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────

    fun startNewScreening() {
        _state.value = ScreeningState.PatientEntry()
    }

    fun goBack() {
        val current = _state.value
        _state.value = when (current) {
            is ScreeningState.DeviceCheck -> ScreeningState.PatientEntry(
                displayName = current.displayName,
                ear = current.ear
            )
            is ScreeningState.PreTestCheck -> ScreeningState.DeviceCheck(
                patientId = current.patientId,
                displayName = current.displayName,
                ear = current.ear,
                probeConnected = true
            )
            is ScreeningState.Testing -> ScreeningState.PreTestCheck(
                patientId = current.patientId,
                displayName = current.displayName,
                ear = current.ear
            )
            is ScreeningState.Result -> ScreeningState.PatientEntry()
            is ScreeningState.ReferForEvaluation -> ScreeningState.PatientEntry()
            is ScreeningState.PatientEntry -> current
        }
    }

    // ── Demo Mode ────────────────────────────────────────────────────────

    fun setDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
    }

    fun toggleDemoMode() {
        _isDemoMode.value = !_isDemoMode.value
    }
}
