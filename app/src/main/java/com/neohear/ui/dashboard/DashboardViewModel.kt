package com.neohear.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neohear.NeoHearApp
import com.neohear.data.dao.DailyTestCount
import com.neohear.data.dao.DashboardDao
import com.neohear.data.dao.ModeCount
import com.neohear.data.entity.Mode
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.TestSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Locale

enum class TimePeriod(val label: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    ALL_TIME("All Time")
}

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    application: Application,
    dashboardDao: DashboardDao
) : AndroidViewModel(application) {

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = application as? NeoHearApp
            val db = app?.database
            return DashboardViewModel(
                application,
                db?.dashboardDao() ?: throw IllegalStateException("AppDatabase not initialized")
            ) as T
        }
    }

    private val _selectedPeriod = MutableStateFlow(TimePeriod.ALL_TIME)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

    fun selectPeriod(period: TimePeriod) {
        _selectedPeriod.value = period
    }

    // Compute time boundaries from the selected period
    private fun periodRange(): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        return when (_selectedPeriod.value) {
            TimePeriod.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis to now
            }
            TimePeriod.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis to now
            }
            TimePeriod.ALL_TIME -> 0L to now
        }
    }

    // ── Reactive data flows ───────────────────────────────────────────

    // Re-derive when period changes
    private val periodFlow = _selectedPeriod.flatMapLatest {
        val (start, end) = periodRange()
        dashboardDao.observeModeCounts(start, end)
    }

    val modeCounts: StateFlow<List<ModeCount>> = periodFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Last 7 days for the bar chart
    private val sevenDaysAgo: Long
        get() {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -6)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

    val dailyCounts: StateFlow<List<DailyTestCount>> = dashboardDao
        .observeDailyTestCounts(sevenDaysAgo)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReferrals: StateFlow<List<Referral>> = dashboardDao
        .observeAllReferrals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined dashboard state
    val dashboardState: StateFlow<DashboardUiState> = combine(
        dashboardDao.observeAllTestSessions(),
        allReferrals,
        dailyCounts,
        periodFlow
    ) { sessions, referrals, dailyCounts, modeCounts ->
        val (start, end) = periodRange()
        val periodSessions = sessions.filter { it.timestamp in start..end }

        val totalTests = periodSessions.size
        val passCount = periodSessions.count { it.result == com.neohear.data.entity.TestResult.PASS }
        val referCount = periodSessions.count { it.result == com.neohear.data.entity.TestResult.REFER }
        val repeatCount = periodSessions.count { it.result == com.neohear.data.entity.TestResult.REPEAT }

        val pendingReferrals = referrals.count {
            it.status != ReferralStatus.COMPLETED && it.status != ReferralStatus.LOST_TO_FOLLOW_UP
        }
        val resolvedReferrals = referrals.count { it.status == ReferralStatus.COMPLETED }
        val lostToFollowUp = referrals.count { it.status == ReferralStatus.LOST_TO_FOLLOW_UP }

        val probeCount = modeCounts.find { it.mode == Mode.PROBE }?.count ?: 0
        val questionnaireCount = modeCounts.find { it.mode == Mode.RISK_QUESTIONNAIRE }?.count ?: 0

        DashboardUiState(
            totalTests = totalTests,
            passCount = passCount,
            referCount = referCount,
            repeatCount = repeatCount,
            passRate = if (totalTests > 0) passCount.toFloat() / totalTests else 0f,
            referRate = if (totalTests > 0) referCount.toFloat() / totalTests else 0f,
            pendingReferrals = pendingReferrals,
            resolvedReferrals = resolvedReferrals,
            lostToFollowUp = lostToFollowUp,
            probeCount = probeCount,
            questionnaireCount = questionnaireCount,
            dailyCounts = dailyCounts,
            selectedPeriod = _selectedPeriod.value,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardUiState(isLoading = true)
    )
}

data class DashboardUiState(
    val totalTests: Int = 0,
    val passCount: Int = 0,
    val referCount: Int = 0,
    val repeatCount: Int = 0,
    val passRate: Float = 0f,
    val referRate: Float = 0f,
    val pendingReferrals: Int = 0,
    val resolvedReferrals: Int = 0,
    val lostToFollowUp: Int = 0,
    val probeCount: Int = 0,
    val questionnaireCount: Int = 0,
    val dailyCounts: List<DailyTestCount> = emptyList(),
    val selectedPeriod: TimePeriod = TimePeriod.ALL_TIME,
    val isLoading: Boolean = true
)
