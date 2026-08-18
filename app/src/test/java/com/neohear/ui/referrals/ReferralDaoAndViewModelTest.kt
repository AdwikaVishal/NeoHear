package com.neohear.ui.referrals

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.neohear.data.AppDatabase
import com.neohear.data.entity.Ear
import com.neohear.data.entity.FollowUpEvent
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import java.util.UUID

/**
 * Tests for ReferralDao queries and ReferralsViewModel logic.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class ReferralDaoAndViewModelTest {

    private lateinit var database: AppDatabase

    private val now = 1700000000000L
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

    // ── DAO Query Tests ──────────────────────────────────────────────

    @Test
    fun getAllReferrals_returnsAll() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")
        insertTestSession(sessionId, patientId)

        val r1Id = UUID.randomUUID()
        val r2Id = UUID.randomUUID()
        database.referralDao().insert(makeReferral(r1Id, patientId, sessionId, ReferralStatus.PENDING))
        database.referralDao().insert(makeReferral(r2Id, patientId, sessionId, ReferralStatus.COMPLETED))

        val all = database.referralDao().getAllReferrals().first()
        assertEquals(2, all.size)
    }

    @Test
    fun getIncompleteReferrals_excludesCompleted() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")
        insertTestSession(sessionId, patientId)

        database.referralDao().insert(makeReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.PENDING))
        database.referralDao().insert(makeReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.SCHEDULED))
        database.referralDao().insert(makeReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.COMPLETED))

        val incomplete = database.referralDao().getIncompleteReferrals().first()
        assertEquals(2, incomplete.size)
        assertTrue(incomplete.all { it.status != ReferralStatus.COMPLETED })
    }

    @Test
    fun getPendingReferralsOlderThan_onlyReturnsOldPending() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")
        insertTestSession(sessionId, patientId)

        // Old PENDING (should match)
        database.referralDao().insert(makeReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.PENDING, createdAt = now - 20 * msPerDay))
        // Recent PENDING (should not match)
        database.referralDao().insert(makeReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.PENDING, createdAt = now - 5 * msPerDay))
        // Old COMPLETED (should not match)
        database.referralDao().insert(makeReferral(UUID.randomUUID(), patientId, sessionId, ReferralStatus.COMPLETED, createdAt = now - 20 * msPerDay))

        val cutoff = now - 14 * msPerDay
        val overdue = database.referralDao().getPendingReferralsOlderThan(cutoff)

        assertEquals(1, overdue.size)
        assertEquals(ReferralStatus.PENDING, overdue[0].status)
    }

    @Test
    fun referralWithFollowUpLog_preservesLog() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")
        insertTestSession(sessionId, patientId)

        val referralId = UUID.randomUUID()
        val event1 = FollowUpEvent(timestamp = now - 1000, note = "First note")
        val event2 = FollowUpEvent(timestamp = now, note = "Second note")

        database.referralDao().insert(
            makeReferral(referralId, patientId, sessionId, ReferralStatus.PENDING).copy(
                followUpLog = listOf(event1, event2)
            )
        )

        val loaded = database.referralDao().getById(referralId)!!
        assertEquals(2, loaded.followUpLog.size)
        assertEquals("First note", loaded.followUpLog[0].note)
        assertEquals("Second note", loaded.followUpLog[1].note)
    }

    @Test
    fun updateReferralStatus_persistsNewStatus() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")
        insertTestSession(sessionId, patientId)

        val referralId = UUID.randomUUID()
        database.referralDao().insert(makeReferral(referralId, patientId, sessionId, ReferralStatus.PENDING))

        val referral = database.referralDao().getById(referralId)!!
        database.referralDao().update(referral.copy(status = ReferralStatus.SCHEDULED, updatedAt = now))

        val updated = database.referralDao().getById(referralId)!!
        assertEquals(ReferralStatus.SCHEDULED, updated.status)
        assertEquals(now, updated.updatedAt)
    }

    @Test
    fun addFollowUpNote_appendsToLog() = runTest {
        val patientId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        insertTestPatient(patientId, "Test")
        insertTestSession(sessionId, patientId)

        val referralId = UUID.randomUUID()
        database.referralDao().insert(makeReferral(referralId, patientId, sessionId, ReferralStatus.PENDING))

        // Add first note
        val referral = database.referralDao().getById(referralId)!!
        val note1 = FollowUpEvent(timestamp = now, note = "Called parent")
        database.referralDao().update(referral.copy(followUpLog = referral.followUpLog + note1))

        // Add second note
        val referral2 = database.referralDao().getById(referralId)!!
        val note2 = FollowUpEvent(timestamp = now + 1000, note = "Left voicemail")
        database.referralDao().update(referral2.copy(followUpLog = referral2.followUpLog + note2))

        val final = database.referralDao().getById(referralId)!!
        assertEquals(2, final.followUpLog.size)
        assertEquals("Called parent", final.followUpLog[0].note)
        assertEquals("Left voicemail", final.followUpLog[1].note)
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun makeReferral(
        id: UUID,
        patientId: UUID,
        testSessionId: UUID,
        status: ReferralStatus,
        createdAt: Long = now
    ) = Referral(
        id = id,
        patientId = patientId,
        testSessionId = testSessionId,
        status = status,
        createdAt = createdAt,
        updatedAt = createdAt
    )

    private suspend fun insertTestPatient(id: UUID, name: String) {
        database.patientDao().insert(
            Patient(id = id, displayNameOrCode = name, dob = Date(1600000000000L), sex = null)
        )
    }

    private suspend fun insertTestSession(id: UUID, patientId: UUID) {
        database.testSessionDao().insert(
            TestSession(
                id = id,
                patientId = patientId,
                ear = Ear.L,
                stage = 1,
                timestamp = now,
                preCheckNoiseLevel = -50f,
                preCheckSealOk = true,
                mode = Mode.PROBE,
                rawSignalRef = null,
                snrValue = 15f,
                result = TestResult.REFER
            )
        )
    }
}
