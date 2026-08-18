package com.neohear.data

import com.neohear.data.entity.Ear
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
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date
import java.util.UUID

class PatientDaoTest : DatabaseTestBase() {

    private fun createPatient(id: UUID = UUID.randomUUID()): Patient {
        return Patient(
            id = id,
            displayNameOrCode = "Baby-$id",
            dob = Date(),
            sex = "M",
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun insertAndReadById() = runTest {
        val patient = createPatient()
        patientDao.insert(patient)

        val retrieved = patientDao.getById(patient.id)
        assertNotNull(retrieved)
        assertEquals(patient.id, retrieved!!.id)
        assertEquals(patient.displayNameOrCode, retrieved.displayNameOrCode)
    }

    @Test
    fun getAllPatients_returnsDescendingByCreatedAt() = runTest {
        val p1 = createPatient().copy(createdAt = 1000L)
        val p2 = createPatient().copy(createdAt = 2000L)
        val p3 = createPatient().copy(createdAt = 3000L)

        patientDao.insert(p1)
        patientDao.insert(p2)
        patientDao.insert(p3)

        val all = patientDao.getAllPatients().first()
        assertEquals(3, all.size)
        assertEquals(3000L, all[0].createdAt)
        assertEquals(2000L, all[1].createdAt)
        assertEquals(1000L, all[2].createdAt)
    }

    @Test
    fun updateModifiesFields() = runTest {
        val patient = createPatient()
        patientDao.insert(patient)

        val updated = patient.copy(displayNameOrCode = "Updated-Name")
        patientDao.update(updated)

        val retrieved = patientDao.getById(patient.id)
        assertEquals("Updated-Name", retrieved!!.displayNameOrCode)
    }

    @Test
    fun deleteRemovesPatient() = runTest {
        val patient = createPatient()
        patientDao.insert(patient)
        patientDao.delete(patient)

        val retrieved = patientDao.getById(patient.id)
        assertNull(retrieved)
    }

    @Test
    fun deletePatient_cascadesToTestSessions() = runTest {
        val patientId = UUID.randomUUID()
        val patient = createPatient(patientId)
        patientDao.insert(patient)

        val session = TestSession(
            id = UUID.randomUUID(),
            patientId = patientId,
            ear = Ear.L,
            stage = 1,
            timestamp = System.currentTimeMillis(),
            preCheckNoiseLevel = -10f,
            preCheckSealOk = true,
            mode = Mode.PROBE,
            rawSignalRef = null,
            snrValue = 15f,
            result = TestResult.PASS
        )
        testSessionDao.insert(session)

        patientDao.delete(patient)

        val retrieved = testSessionDao.getById(session.id)
        assertNull("TestSession should be cascade-deleted with Patient", retrieved)
    }

    @Test
    fun deletePatient_cascadesToReferrals() = runTest {
        val patientId = UUID.randomUUID()
        val patient = createPatient(patientId)
        patientDao.insert(patient)

        val sessionId = UUID.randomUUID()
        val session = TestSession(
            id = sessionId,
            patientId = patientId,
            ear = Ear.R,
            stage = 1,
            timestamp = System.currentTimeMillis(),
            preCheckNoiseLevel = -12f,
            preCheckSealOk = true,
            mode = Mode.PROBE,
            rawSignalRef = null,
            snrValue = 3f,
            result = TestResult.REFER
        )
        testSessionDao.insert(session)

        val referral = Referral(
            id = UUID.randomUUID(),
            patientId = patientId,
            testSessionId = sessionId,
            status = ReferralStatus.PENDING
        )
        referralDao.insert(referral)

        patientDao.delete(patient)

        val retrieved = referralDao.getById(referral.id)
        assertNull("Referral should be cascade-deleted with Patient", retrieved)
    }
}
