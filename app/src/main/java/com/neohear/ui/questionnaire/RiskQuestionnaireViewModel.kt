package com.neohear.ui.questionnaire

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neohear.NeoHearApp
import com.neohear.data.entity.RiskLevel
import com.neohear.data.entity.RiskQuestionnaireResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the risk-factor questionnaire flow.
 *
 * Manages the one-at-a-time question navigation, answer collection,
 * risk computation, and persistence via the RiskQuestionnaireResponse DAO.
 */
class RiskQuestionnaireViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as NeoHearApp).database.riskQuestionnaireResponseDao()

    private val factors = RiskFactorCatalog.factors

    /** UI state for the questionnaire screen. */
    data class UiState(
        val currentIndex: Int = 0,
        val answers: Map<String, String> = emptyMap(),
        val isComplete: Boolean = false,
        val riskLevel: RiskLevel? = null,
        val savedResponseId: UUID? = null,
        val isSaving: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /** Whether we are on the last question (for showing "Finish" vs "Next"). */
    val isLastQuestion: Boolean
        get() = _uiState.value.currentIndex >= factors.size - 1

    /** Total number of questions. */
    val totalQuestions: Int
        get() = factors.size

    /** The current question to display. */
    val currentFactor: RiskFactor
        get() = factors[_uiState.value.currentIndex]

    /** Progress as a float 0..1. */
    val progress: Float
        get() = (_uiState.value.currentIndex + 1).toFloat() / factors.size

    /**
     * Record an answer for the current question and advance to the next.
     */
    fun answerCurrent(answer: String) {
        val state = _uiState.value
        val factor = factors[state.currentIndex]
        val newAnswers = state.answers + (factor.id to answer)

        if (state.currentIndex >= factors.size - 1) {
            // Last question — complete the questionnaire
            val riskLevel = computeRiskLevel(newAnswers)
            _uiState.update {
                it.copy(
                    answers = newAnswers,
                    isComplete = true,
                    riskLevel = riskLevel
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    answers = newAnswers,
                    currentIndex = it.currentIndex + 1
                )
            }
        }
    }

    /**
     * Save the completed questionnaire response to the database.
     *
     * @param patientId The patient this questionnaire belongs to. Pass null for standalone mode.
     */
    fun saveResponse(patientId: UUID? = null) {
        val state = _uiState.value
        if (!state.isComplete || state.riskLevel == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val response = RiskQuestionnaireResponse(
                id = UUID.randomUUID(),
                patientId = patientId ?: UUID.randomUUID(), // placeholder for standalone mode
                timestamp = System.currentTimeMillis(),
                answers = state.answers,
                riskLevel = state.riskLevel
            )

            dao.insert(response)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    savedResponseId = response.id
                )
            }
        }
    }

    /**
     * Reset the questionnaire to start over.
     */
    fun reset() {
        _uiState.value = UiState()
    }
}
