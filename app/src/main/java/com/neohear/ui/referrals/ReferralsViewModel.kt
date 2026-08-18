package com.neohear.ui.referrals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neohear.NeoHearApp
import com.neohear.data.dao.PatientDao
import com.neohear.data.dao.ReferralDao
import com.neohear.data.dao.TestSessionDao
import com.neohear.data.entity.Ear
import com.neohear.data.entity.FollowUpEvent
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the Referrals list and detail screens.
 */
class ReferralsViewModel(
    application: Application,
    private val referralDao: ReferralDao,
    private val patientDao: PatientDao,
    private val testSessionDao: TestSessionDao
) : AndroidViewModel(application) {

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = application as? NeoHearApp
            val db = app?.database
            return ReferralsViewModel(
                application,
                db?.referralDao() ?: throw IllegalStateException("AppDatabase not initialized"),
                db.patientDao(),
                db.testSessionDao()
            ) as T
        }
    }

    private val _listState = MutableStateFlow(ReferralListState())
    val listState: StateFlow<ReferralListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<ReferralDetailState>(ReferralDetailState.Loading)
    val detailState: StateFlow<ReferralDetailState> = _detailState.asStateFlow()

    init {
        loadReferrals()
    }

    private fun loadReferrals() {
        viewModelScope.launch {
            referralDao.getAllReferrals().collect { referrals ->
                val items = referrals.map { referral ->
                    val patient = patientDao.getById(referral.patientId)
                    val session = testSessionDao.getById(referral.testSessionId)
                    ReferralListItem(
                        referral = referral,
                        patientName = patient?.displayNameOrCode ?: "Unknown",
                        patientDob = patient?.dob,
                        ear = session?.ear
                    )
                }
                _listState.update { it.copy(referrals = items, isLoading = false) }
            }
        }
    }

    fun loadReferralDetail(referralId: UUID) {
        _detailState.value = ReferralDetailState.Loading
        viewModelScope.launch {
            val referral = referralDao.getById(referralId)
            if (referral == null) {
                _detailState.value = ReferralDetailState.Error("Referral not found")
                return@launch
            }
            val patient = patientDao.getById(referral.patientId)
            val session = testSessionDao.getById(referral.testSessionId)
            _detailState.value = ReferralDetailState.Loaded(
                referral = referral,
                patientName = patient?.displayNameOrCode ?: "Unknown",
                patientDob = patient?.dob,
                ear = session?.ear
            )
        }
    }

    fun updateStatus(referralId: UUID, newStatus: ReferralStatus) {
        viewModelScope.launch {
            val referral = referralDao.getById(referralId) ?: return@launch
            val updated = referral.copy(
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )
            referralDao.update(updated)
            loadReferralDetail(referralId)
        }
    }

    fun addFollowUpNote(referralId: UUID, note: String) {
        if (note.isBlank()) return
        viewModelScope.launch {
            val referral = referralDao.getById(referralId) ?: return@launch
            val event = FollowUpEvent(
                timestamp = System.currentTimeMillis(),
                note = note.trim()
            )
            val updated = referral.copy(
                followUpLog = referral.followUpLog + event,
                updatedAt = System.currentTimeMillis()
            )
            referralDao.update(updated)
            loadReferralDetail(referralId)
        }
    }
}

/** UI state for the referrals list screen. */
data class ReferralListState(
    val referrals: List<ReferralListItem> = emptyList(),
    val isLoading: Boolean = true
)

/** A single referral item enriched with patient + session info. */
data class ReferralListItem(
    val referral: Referral,
    val patientName: String,
    val patientDob: java.util.Date?,
    val ear: Ear? = null
)

/** Sealed UI state for the referral detail screen. */
sealed class ReferralDetailState {
    data object Loading : ReferralDetailState()
    data class Error(val message: String) : ReferralDetailState()
    data class Loaded(
        val referral: Referral,
        val patientName: String,
        val patientDob: java.util.Date?,
        val ear: Ear? = null
    ) : ReferralDetailState()
}
