package com.neohear.reminder

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.neohear.data.AppDatabase
import com.neohear.data.entity.Ear
import com.neohear.data.entity.Mode
import com.neohear.data.entity.Patient
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.TestResult
import com.neohear.data.entity.TestSession
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
 * Tests for [FollowUpReminder].
 *
 * Verifies that:
 * - A PENDING referral created 14+ days ago triggers a reminder
 * - A COMPLETED referral does NOT trigger a reminder
 * - A SCHEDULED referral does NOT trigger a reminder
 * - A PENDING referral less than 14 days old does NOT trigger a reminder
 * - Multiple overdue referrals all trigger reminders
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class FollowUpReminderTest {

    private lateinit var database: AppDatabase

    // Fixed "now" timestamp for deterministic time calculations
    private val now = 1700000000000L // Nov 15, 2023
    private val msPerDay = 24L * 60 * 60 * 1000

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        SimulatedSmsLog.clear()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun pendingReferral_overdue_triggersReminder() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        val referralId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Alpha")
        insertTestSession(sessionId, patientId)
        insertReferral(referralId, patientId, sessionId, ReferralStatus.PENDING, createdAt = now - 20 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(1, events.size)
        assertEquals("Baby Alpha", events[0].patientName)
        assertEquals(referralId, events[0].referralId)
        assertEquals(20, events[0].daysPending)

        // Verify SMS was logged
        val smsEntries = SimulatedSmsLog.getAll()
        assertTrue(smsEntries.isNotEmpty())
        assertTrue(smsEntries[0].message.contains("Baby Alpha"))
        assertTrue(smsEntries[0].message.contains("SIMULATED SMS"))
    }

    @Test
    fun completedReferral_doesNotTriggerReminder() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Beta")
        insertTestSession(sessionId, patientId)
        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.COMPLETED, createdAt = now - 30 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(0, events.size)
        assertTrue(SimulatedSmsLog.getAll().isEmpty())
    }

    @Test
    fun scheduledReferral_doesNotTriggerReminder() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Gamma")
        insertTestSession(sessionId, patientId)
        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.SCHEDULED, createdAt = now - 20 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(0, events.size)
        assertTrue(SimulatedSmsLog.getAll().isEmpty())
    }

    @Test
    fun pendingReferral_notYetOverdue_doesNotTriggerReminder() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Delta")
        insertTestSession(sessionId, patientId)
        // Created 10 days ago — not yet overdue (threshold = 14)
        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.PENDING, createdAt = now - 10 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(0, events.size)
        assertTrue(SimulatedSmsLog.getAll().isEmpty())
    }

    @Test
    fun pendingReferral_exactlyAtThreshold_triggersReminder() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        val referralId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Epsilon")
        insertTestSession(sessionId, patientId)
        // Created exactly 14 days ago — should trigger
        insertReferral(referralId, patientId, sessionId, ReferralStatus.PENDING, createdAt = now - 14 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(1, events.size)
        assertEquals("Baby Epsilon", events[0].patientName)
        assertEquals(14, events[0].daysPending)
    }

    @Test
    fun multipleOverdueReferrals_allTriggerReminders() = runTest {
        val patient1Id = UUID.randomUUID()
        val patient2Id = UUID.randomUUID()
        val session1Id = UUID.randomUUID()
        val session2Id = UUID.randomUUID()

        insertTestPatient(patient1Id, "Baby One")
        insertTestPatient(patient2Id, "Baby Two")
        insertTestSession(session1Id, patient1Id)
        insertTestSession(session2Id, patient2Id)

        insertReferral(UUID.randomUUID(), patient1Id, session1Id, ReferralStatus.PENDING, createdAt = now - 20 * msPerDay)
        insertReferral(UUID.randomUUID(), patient2Id, session2Id, ReferralStatus.PENDING, createdAt = now - 30 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(2, events.size)
        val names = events.map { it.patientName }.toSet()
        assertTrue(names.contains("Baby One"))
        assertTrue(names.contains("Baby Two"))

        // Both SMS entries should be logged
        assertEquals(2, SimulatedSmsLog.getAll().size)
    }

    @Test
    fun lostToFollowUp_doesNotTriggerReminder() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Zeta")
        insertTestSession(sessionId, patientId)
        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.LOST_TO_FOLLOW_UP, createdAt = now - 30 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        val events = reminder.checkAndNotify()

        assertEquals(0, events.size)
    }

    @Test
    fun simulatedSmsLog_containsCorrectFormat() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()

        insertTestPatient(patientId, "Baby Format")
        insertTestSession(sessionId, patientId)
        insertReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.PENDING, createdAt = now - 15 * msPerDay)

        val reminder = FollowUpReminder(
            context = ApplicationProvider.getApplicationContext(),
            database = database,
            pendingThresholdDays = 14,
            now = now
        )

        reminder.checkAndNotify()

        val smsEntries = SimulatedSmsLog.getAll()
        assertEquals(1, smsEntries.size)
        val msg = smsEntries[0].message
        assertTrue(msg.startsWith("[SIMULATED SMS]"))
        assertTrue(msg.contains("END SIMULATED SMS"))
        assertTrue(msg.contains("NeoHear hearing screening referral"))
        assertTrue(msg.contains("PENDING for 15 days"))
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private suspend fun insertTestPatient(id: UUID, name: String) {
        database.patientDao().insert(
            Patient(
                id = id,
                displayNameOrCode = name,
                dob = Date(1600000000000L),
                sex = null
            )
        )
    }

    private suspend fun insertTestSession(id: UUID, patientId: UUID) {
        database.testSessionDao().insert(
            TestSession(
                id = id,
                patientId = patientId,
                ear = Ear.L,
                stage = 1,
                timestamp = System.currentTimeMillis(),
                preCheckNoiseLevel = -50f,
                preCheckSealOk = true,
                mode = Mode.PROBE,
                rawSignalRef = null,
                snrValue = 15f,
                result = TestResult.REFER
            )
        )
    }

    private suspend fun insertReferral(
        id: UUID,
        patientId: UUID,
        testSessionId: UUID,
        status: ReferralStatus,
        createdAt: Long
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
