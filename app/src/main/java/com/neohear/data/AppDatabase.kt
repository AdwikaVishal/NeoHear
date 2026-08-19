package com.neohear.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.neohear.data.converter.Converters
import com.neohear.data.dao.CryAnalysisDao
import com.neohear.data.dao.DashboardDao
import com.neohear.data.dao.PatientDao
import com.neohear.data.dao.ReferralDao
import com.neohear.data.dao.RiskQuestionnaireResponseDao
import com.neohear.data.dao.TestSessionDao
import com.neohear.data.entity.CryAnalysis
import com.neohear.data.entity.Patient
import com.neohear.data.entity.Referral
import com.neohear.data.entity.RiskQuestionnaireResponse
import com.neohear.data.entity.TestSession
import com.neohear.data.keystore.PassphraseManager
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        Patient::class,
        TestSession::class,
        Referral::class,
        RiskQuestionnaireResponse::class,
        com.neohear.data.entity.SyncRecord::class,
        CryAnalysis::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun testSessionDao(): TestSessionDao
    abstract fun referralDao(): ReferralDao
    abstract fun riskQuestionnaireResponseDao(): RiskQuestionnaireResponseDao
    abstract fun dashboardDao(): DashboardDao
    abstract fun syncDao(): com.neohear.data.dao.SyncDao
    abstract fun cryAnalysisDao(): CryAnalysisDao

    companion object {

        init {
            try {
                // Ensure dependencies are loaded in order
                System.loadLibrary("c++_shared")
                System.loadLibrary("sqlcipher")
            } catch (e: UnsatisfiedLinkError) {
                android.util.Log.e("AppDatabase", "Failed to load sqlcipher: ${e.message}")
            }
        }

        private const val DATABASE_NAME = "nehear.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val passphrase = PassphraseManager.getPassphrase(context)
            val factory = SupportOpenHelperFactory(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
