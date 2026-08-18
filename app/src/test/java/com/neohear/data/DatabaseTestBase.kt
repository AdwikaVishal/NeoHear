package com.neohear.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.neohear.data.dao.DashboardDao
import com.neohear.data.dao.PatientDao
import com.neohear.data.dao.ReferralDao
import com.neohear.data.dao.RiskQuestionnaireResponseDao
import com.neohear.data.dao.TestSessionDao
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Base class for Room database unit tests using Robolectric.
 *
 * Creates an in-memory Room database WITHOUT SQLCipher — we test DAO logic,
 * not encryption. Uses a plain Application to avoid triggering PassphraseManager
 * (which requires AndroidKeyStore and doesn't work under Robolectric).
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
abstract class DatabaseTestBase {

    protected lateinit var database: AppDatabase
    protected lateinit var patientDao: PatientDao
    protected lateinit var testSessionDao: TestSessionDao
    protected lateinit var referralDao: ReferralDao
    protected lateinit var riskQuestionnaireResponseDao: RiskQuestionnaireResponseDao
    protected lateinit var dashboardDao: DashboardDao

    @Before
    open fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        patientDao = database.patientDao()
        testSessionDao = database.testSessionDao()
        referralDao = database.referralDao()
        riskQuestionnaireResponseDao = database.riskQuestionnaireResponseDao()
        dashboardDao = database.dashboardDao()
    }

    @After
    open fun tearDown() {
        database.close()
    }
}
