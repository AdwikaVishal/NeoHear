package com.neohear.data

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date
import java.util.UUID

class ReferralDaoTest : DatabaseTestBase() {

    private suspend fun createPatientAndSession(): Pair<Patient, TestSession> {
        val patientId = UUID.randomUUID()
        val patient = Patient(
            id = patientId,
            displayNameOrCode = "Test-Baby",
            dob = Date(),
            sex = null,
            createdAt = System.currentTimeMillis()
        )
        patientDao.insert(patient)

        val session = TestSession(
            id = UUID.randomUUID(),
            patientId = patientId,
            ear = Ear.L,
            stage = 1,
            timestamp = System.currentTimeMillis(),
            preCheckNoiseLevel = -8f,
            preCheckSealOk = true,
            mode = Mode.PROBE,
            rawSignalRef = null,
            snrValue = 2f,
            result = TestResult.REFER
        )
        testSessionDao.insert(session)
        return Pair(patient, session)
    }

    @Test
    fun insertAndGetById() = runTest {
        val (patient, session) = createPatientAndSession()

        val referral = Referral(
            id = UUID.randomUUID(),
            patientId = patient.id,
            testSessionId = session.id,
            status = ReferralStatus.PENDING
        )
        referralDao.insert(referral)

        val retrieved = referralDao.getById(referral.id)
        assertNotNull(retrieved)
        assertEquals(referral.id, retrieved!!.id)
        assertEquals(ReferralStatus.PENDING, retrieved.status)
    }

    @Test
    fun getIncompleteReferrals_excludesCompleted() = runTest {
        val (patient, session) = createPatientAndSession()

        val pendingReferral = Referral(
            id = UUID.randomUUID(),
            patientId = patient.id,
            testSessionId = session.id,
            status = ReferralStatus.PENDING
        )
        referralDao.insert(pendingReferral)

        val completedReferral = Referral(
            id = UUID.randomUUID(),
            patientId = patient.id,
            testSessionId = session.id,
            status = ReferralStatus.COMPLETED
        )
        referralDao.insert(completedReferral)

        val incomplete = referralDao.getIncompleteReferrals().first()
        assertEquals(1, incomplete.size)
        assertEquals(ReferralStatus.PENDING, incomplete[0].status)
    }

    @Test
    fun getIncompleteReferrals_includesAllNonCompletedStatuses() = runTest {
        val (patient, session) = createPatientAndSession()

        val statuses = listOf(
            ReferralStatus.PENDING,
            ReferralStatus.SCHEDULED,
            ReferralStatus.LOST_TO_FOLLOW_UP
        )
        for (status in statuses) {
            referralDao.insert(
                Referral(
                    id = UUID.randomUUID(),
                    patientId = patient.id,
                    testSessionId = session.id,
                    status = status
                )
            )
        }

        val incomplete = referralDao.getIncompleteReferrals().first()
        assertEquals(3, incomplete.size)
    }

    @Test
    fun updateReferralStatus() = runTest {
        val (patient, session) = createPatientAndSession()

        val referral = Referral(
            id = UUID.randomUUID(),
            patientId = patient.id,
            testSessionId = session.id,
            status = ReferralStatus.PENDING
        )
        referralDao.insert(referral)

        val updated = referral.copy(
            status = ReferralStatus.SCHEDULED,
            updatedAt = System.currentTimeMillis()
        )
        referralDao.update(updated)

        val retrieved = referralDao.getById(referral.id)
        assertEquals(ReferralStatus.SCHEDULED, retrieved!!.status)
    }

    @Test
    fun referralWithFollowUpLog() = runTest {
        val (patient, session) = createPatientAndSession()

        val log = listOf(
            FollowUpEvent(timestamp = 1000L, note = "Called parent"),
            FollowUpEvent(timestamp = 2000L, note = "Scheduled visit")
        )
        val referral = Referral(
            id = UUID.randomUUID(),
            patientId = patient.id,
            testSessionId = session.id,
            status = ReferralStatus.SCHEDULED,
            followUpLog = log
        )
        referralDao.insert(referral)

        val retrieved = referralDao.getById(referral.id)
        assertEquals(2, retrieved!!.followUpLog.size)
        assertEquals("Called parent", retrieved.followUpLog[0].note)
        assertEquals("Scheduled visit", retrieved.followUpLog[1].note)
    }

    @Test
    fun getReferralsForPatient() = runTest {
        val (patient, session) = createPatientAndSession()

        referralDao.insert(
            Referral(
                id = UUID.randomUUID(),
                patientId = patient.id,
                testSessionId = session.id,
                status = ReferralStatus.PENDING
            )
        )
        referralDao.insert(
            Referral(
                id = UUID.randomUUID(),
                patientId = patient.id,
                testSessionId = session.id,
                status = ReferralStatus.SCHEDULED
            )
        )

        val referrals = referralDao.getReferralsForPatient(patient.id).first()
        assertEquals(2, referrals.size)
    }

    @Test
    fun referResult_linksToNewReferral() = runTest {
        val (patient, session) = createPatientAndSession()

        assertEquals(TestResult.REFER, session.result)

        val referral = Referral(
            id = UUID.randomUUID(),
            patientId = patient.id,
            testSessionId = session.id,
            status = ReferralStatus.PENDING
        )
        referralDao.insert(referral)

        val retrieved = referralDao.getById(referral.id)
        assertNotNull(retrieved)
        assertEquals(session.id, retrieved!!.testSessionId)
        assertEquals(patient.id, retrieved.patientId)
        assertTrue(retrieved.status != ReferralStatus.COMPLETED)
    }
}
