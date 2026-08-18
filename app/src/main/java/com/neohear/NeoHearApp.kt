package com.neohear

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.neohear.data.AppDatabase

class NeoHearApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        // Ensure all native libraries are loaded in the correct order
        val libs = listOf("c++_shared", "sqlcipher", "neohear")
        for (lib in libs) {
            try {
                System.loadLibrary(lib)
                android.util.Log.i("NeoHearApp", "Loaded native library: $lib")
            } catch (e: UnsatisfiedLinkError) {
                android.util.Log.e("NeoHearApp", "Failed to load native library: $lib, error: ${e.message}")
            }
        }

        // Trigger SQLCipher initialization
        try {
            net.zetetic.database.sqlcipher.SQLiteDatabase.hasCodec()
            android.util.Log.i("NeoHearApp", "SQLCipher initialized successfully")
        } catch (e: Throwable) {
            android.util.Log.e("NeoHearApp", "SQLCipher initialization check failed: ${e.message}")
        }

        database = AppDatabase.getInstance(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val reminderChannel = NotificationChannel(
            CHANNEL_REFERRAL_REMINDER,
            "Referral Follow-up Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders for pending referrals that need follow-up"
        }

        manager.createNotificationChannel(reminderChannel)
    }

    companion object {
        const val CHANNEL_REFERRAL_REMINDER = "referral_reminder"
    }
}
