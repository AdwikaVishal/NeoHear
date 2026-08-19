package com.neohear.ui.cry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neohear.NeoHearApp
import com.neohear.audio.CryAnalysisResult
import com.neohear.audio.CryAnalyzer
import com.neohear.data.dao.CryAnalysisDao
import com.neohear.data.dao.PatientDao
import com.neohear.data.entity.CryAnalysis
import com.neohear.data.entity.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CryAnalysisViewModel(
    application: Application,
    private val cryAnalysisDao: CryAnalysisDao,
    private val patientDao: PatientDao
) : AndroidViewModel(application) {

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = application as? NeoHearApp
                ?: throw IllegalStateException("Application must be NeoHearApp")
            val db = app.database
            return CryAnalysisViewModel(
                application,
                db.cryAnalysisDao(),
                db.patientDao()
            ) as T
        }
    }

    sealed class UiState {
        data object Idle : UiState()
        data object Recording : UiState()
        data object Analyzing : UiState()
        data class Result(
            val data: CryAnalysisResult,
            val savedId: UUID
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val patients: StateFlow<List<Patient>> = patientDao.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentPatientId: UUID = UUID.randomUUID()

    fun selectPatient(patientId: UUID) {
        currentPatientId = patientId
    }

    fun createPatient(name: String, dob: Long) {
        val patient = Patient(
            id = UUID.randomUUID(),
            displayNameOrCode = name,
            dob = java.util.Date(dob),
            sex = null
        )
        currentPatientId = patient.id
        viewModelScope.launch {
            patientDao.insert(patient)
        }
    }

    fun startAnalysis() {
        viewModelScope.launch {
            _uiState.value = UiState.Recording
            _uiState.value = UiState.Analyzing

            val result = CryAnalyzer.analyze()
            if (result == null) {
                _uiState.value = UiState.Error(
                    "Recording failed or too short. Ensure microphone permission is granted and the baby is crying."
                )
                return@launch
            }

            val id = UUID.randomUUID()
            val entity = CryAnalysis(
                id = id,
                patientId = currentPatientId,
                avgPitchHz = result.avgPitchHz,
                pitchStdDev = result.pitchStdDev,
                avgEnergyDb = result.avgEnergyDb,
                jitter = result.jitter,
                shimmer = result.shimmer,
                voicingRatio = result.voicingRatio,
                riskFlags = result.riskFlags
            )
            cryAnalysisDao.insert(entity)
            _uiState.value = UiState.Result(result, id)
        }
    }

    fun reset() {
        _uiState.value = UiState.Idle
    }
}
