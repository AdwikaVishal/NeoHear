package com.neohear.data

import com.neohear.data.entity.Ear
import com.neohear.data.entity.Mode
import com.neohear.data.entity.Patient
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.TestResult
import com.neohear.data.entity.TestSession
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.Date
import java.util.UUID

class DashboardDaoTest : DatabaseTestBase() {

    private suspend fun createPatient(): Patient {
        val patient = Patient(
            id = UUID.randomUUID(),
            displayNameOrCode = "Dashboard-Test",
            dob = Date(),
            sex = "F",
            createdAt = System.currentTimeMillis()
        )
        patientDao.insert(patient)
        return patient
    }

    private suspend fun createSession(
        patientId: UUID,
        result: TestResult,
        timestamp: Long
    ): TestSession {
        val session = TestSession(
            id = UUID.randomUUID(),
            patientId = patientId,
            ear = Ear.L,
            stage = 1,
            timestamp = timestamp,
            preCheckNoiseLevel = -10f,
            preCheckSealOk = true,
            mode = Mode.PROBE,
            rawSignalRef = null,
            snrValue = if (result == TestResult.PASS) 15f else 2f,
            result = result
        )
        testSessionDao.insert(session)
        return session
    }

    @Test
    fun dashboardCounts_emptyDatabase() = runTest {
        val counts = dashboardDao.getDashboardCounts(0L, System.currentTimeMillis())
        assertNotNull(counts)
        assertEquals(0, counts!!.totalTests)
        assertEquals(0, counts.passCount)
        assertEquals(0, counts.referCount)
        assertEquals(0, counts.pendingReferrals)
        assertEquals(0, counts.resolvedReferrals)
    }

    @Test
    fun dashboardCounts_testSessionAggregation() = runTest {
        val patient = createPatient()
        val now = System.currentTimeMillis()

        createSession(patient.id, TestResult.PASS, now - 5000)
        createSession(patient.id, TestResult.PASS, now - 4000)
        createSession(patient.id, TestResult.REFER, now - 3000)
        createSession(patient.id, TestResult.REPEAT, now - 2000)

        val counts = dashboardDao.getDashboardCounts(now - 10000, now)
        assertNotNull(counts)
        assertEquals(4, counts!!.totalTests)
        assertEquals(2, counts.passCount)
        assertEquals(1, counts.referCount)
    }

    @Test
    fun dashboardCounts_dateRangeFiltering() = runTest {
        val patient = createPatient()
        val now = System.currentTimeMillis()

        createSession(patient.id, TestResult.PASS, now - 20000)
        createSession(patient.id, TestResult.REFER, now - 10000)
        createSession(patient.id, TestResult.PASS, now - 5000)

        val counts = dashboardDao.getDashboardCounts(now - 15000, now)
        assertNotNull(counts)
        assertEquals(2, counts!!.totalTests)
        assertEquals(1, counts.passCount)
        assertEquals(1, counts.referCount)
    }

    @Test
    fun dashboardCounts_referralStatusAggregation() = runTest {
        val patient = createPatient()
        val session = createSession(patient.id, TestResult.REFER, System.currentTimeMillis() - 1000)

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
        referralDao.insert(
            Referral(
                id = UUID.randomUUID(),
                patientId = patient.id,
                testSessionId = session.id,
                status = ReferralStatus.COMPLETED
            )
        )

        val counts = dashboardDao.getDashboardCounts(0L, System.currentTimeMillis())
        assertNotNull(counts)
        assertEquals(2, counts!!.pendingReferrals)
        assertEquals(1, counts.resolvedReferrals)
    }

    @Test
    fun dashboardCounts_lostToFollowUp_countsAsPending() = runTest {
        val patient = createPatient()
        val session = createSession(patient.id, TestResult.REFER, System.currentTimeMillis() - 1000)

        referralDao.insert(
            Referral(
                id = UUID.randomUUID(),
                patientId = patient.id,
                testSessionId = session.id,
                status = ReferralStatus.LOST_TO_FOLLOW_UP
            )
        )

        val counts = dashboardDao.getDashboardCounts(0L, System.currentTimeMillis())
        assertNotNull(counts)
        assertEquals(1, counts!!.pendingReferrals)
        assertEquals(0, counts.resolvedReferrals)
    }
}
