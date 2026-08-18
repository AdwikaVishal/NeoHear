package com.neohear.ui.dashboard

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.neohear.data.AppDatabase
import com.neohear.data.dao.DailyTestCount
import com.neohear.data.entity.Ear
import com.neohear.data.entity.Mode
import com.neohear.data.entity.Patient
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.TestResult
import com.neohear.data.entity.TestSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import java.util.UUID

/**
 * Tests for DashboardDao reactive queries and DashboardViewModel period filtering logic.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class DashboardViewModelTest {

    private lateinit var database: AppDatabase

    private val now = System.currentTimeMillis()
    private val msPerDay = 24L * 60 * 60 * 1000

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ── DAO query tests ─────────────────────────────────────────────

    @Test
    fun observeAllTestSessions_returnsAll() = runTest {
        val patientId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")

        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.PASS, now - 2 * msPerDay)
        insertTestSession(UUID.randomUUID(), patientId, Mode.RISK_QUESTIONNAIRE, TestResult.REFER, now - 1 * msPerDay)

        val sessions = database.dashboardDao().observeAllTestSessions().first()
        assertEquals(2, sessions.size)
    }

    @Test
    fun observeAllReferrals_returnsAll() = runTest {
        val patientId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")

        // Create a session first so the foreign key is satisfied
        val sessionId = UUID.randomUUID()
        insertTestSession(sessionId, patientId, Mode.PROBE, TestResult.REFER)

        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.PENDING)
        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.COMPLETED)

        val referrals = database.dashboardDao().observeAllReferrals().first()
        assertEquals(2, referrals.size)
    }

    @Test
    fun observeModeCounts_filtersByTimeWindow() = runTest {
        val patientId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")

        // 2 PROBE sessions today, 1 RISK_QUESTIONNAIRE today
        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.PASS, now)
        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.PASS, now - 1000)
        insertTestSession(UUID.randomUUID(), patientId, Mode.RISK_QUESTIONNAIRE, TestResult.PASS, now - 2000)

        // 1 PROBE session 30 days ago (outside window)
        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.PASS, now - 30 * msPerDay)

        val startOfDay = now - (now % msPerDay) // approximate start of today
        val modeCounts = database.dashboardDao().observeModeCounts(startOfDay, now).first()

        val probeCount = modeCounts.find { it.mode == Mode.PROBE }?.count ?: 0
        val questionCount = modeCounts.find { it.mode == Mode.RISK_QUESTIONNAIRE }?.count ?: 0

        assertEquals(2, probeCount)
        assertEquals(1, questionCount)
    }

    @Test
    fun observeDailyTestCounts_groupsCorrectly() = runTest {
        val patientId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")

        // 2 PASS + 1 REFER on "today"
        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.PASS, now)
        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.PASS, now - 1000)
        insertTestSession(UUID.randomUUID(), patientId, Mode.PROBE, TestResult.REFER, now - 2000)

        val since = now - 7 * msPerDay
        val dailyCounts = database.dashboardDao().observeDailyTestCounts(since).first()

        assertTrue(dailyCounts.isNotEmpty())
        val todayCount = dailyCounts.last()
        assertEquals(2, todayCount.passCount)
        assertEquals(1, todayCount.referCount)
    }

    // ── Period filtering logic tests ─────────────────────────────────

    @Test
    fun selectPeriod_updatesSelectedPeriod() {
        val vm = createViewModel()

        assertEquals(TimePeriod.ALL_TIME, vm.selectedPeriod.value)

        vm.selectPeriod(TimePeriod.TODAY)
        assertEquals(TimePeriod.TODAY, vm.selectedPeriod.value)

        vm.selectPeriod(TimePeriod.THIS_WEEK)
        assertEquals(TimePeriod.THIS_WEEK, vm.selectedPeriod.value)

        vm.selectPeriod(TimePeriod.ALL_TIME)
        assertEquals(TimePeriod.ALL_TIME, vm.selectedPeriod.value)
    }

    @Test
    fun selectedPeriod_label_isCorrect() {
        assertEquals("Today", TimePeriod.TODAY.label)
        assertEquals("This Week", TimePeriod.THIS_WEEK.label)
        assertEquals("All Time", TimePeriod.ALL_TIME.label)
    }

    // ── DashboardUiState computation tests ───────────────────────────

    @Test
    fun dashboardState_computesRates_correctly() {
        // Simulate what the ViewModel does when computing rates
        val totalTests = 10
        val passCount = 7
        val referCount = 2

        val passRate = if (totalTests > 0) passCount.toFloat() / totalTests else 0f
        val referRate = if (totalTests > 0) referCount.toFloat() / totalTests else 0f

        assertEquals(0.7f, passRate, 0.01f)
        assertEquals(0.2f, referRate, 0.01f)
    }

    @Test
    fun dashboardState_zeroTests_noDivisionByZero() {
        val totalTests = 0
        val passCount = 0

        val passRate = if (totalTests > 0) passCount.toFloat() / totalTests else 0f

        assertEquals(0f, passRate)
    }

    @Test
    fun dashboardState_referralCounts() {
        val state = DashboardUiState(
            pendingReferrals = 3,
            resolvedReferrals = 5,
            lostToFollowUp = 1
        )
        assertEquals(3, state.pendingReferrals)
        assertEquals(5, state.resolvedReferrals)
        assertEquals(1, state.lostToFollowUp)
    }

    @Test
    fun dashboardState_modeCounts() {
        val state = DashboardUiState(
            probeCount = 15,
            questionnaireCount = 5
        )
        assertEquals(15, state.probeCount)
        assertEquals(5, state.questionnaireCount)
    }

    @Test
    fun dashboardState_dailyCounts() {
        val d1 = DailyTestCount(dayStart = 1000, passCount = 3, referCount = 1)
        val d2 = DailyTestCount(dayStart = 2000, passCount = 5, referCount = 2)

        val state = DashboardUiState(dailyCounts = listOf(d1, d2))
        assertEquals(2, state.dailyCounts.size)
        assertEquals(3, state.dailyCounts[0].passCount)
        assertEquals(2, state.dailyCounts[1].referCount)
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun createViewModel(): DashboardViewModel {
        val context = ApplicationProvider.getApplicationContext<Application>()
        return DashboardViewModel(context, database.dashboardDao())
    }

    private suspend fun insertTestPatient(id: UUID, name: String) {
        database.patientDao().insert(
            Patient(id = id, displayNameOrCode = name, dob = Date(1600000000000L), sex = null)
        )
    }

    private suspend fun insertTestSession(
        id: UUID,
        patientId: UUID,
        mode: Mode,
        result: TestResult,
        timestamp: Long = now
    ) {
        database.testSessionDao().insert(
            TestSession(
                id = id,
                patientId = patientId,
                ear = Ear.L,
                stage = 1,
                timestamp = timestamp,
                preCheckNoiseLevel = -50f,
                preCheckSealOk = true,
                mode = mode,
                rawSignalRef = null,
                snrValue = 15f,
                result = result
            )
        )
    }

    private suspend fun insertReferral(
        id: UUID,
        patientId: UUID,
        testSessionId: UUID,
        status: ReferralStatus,
        createdAt: Long = now
    ) {
        database.referralDao().insert(
            Referral(
                id = id,
                patientId = patientId,
                testSessionId = testSessionId,
                status = status,
                createdAt = createdAt,
                updatedAt = createdAt
            )
        )
    }
}
