package com.neohear.sync

import androidx.test.core.app.ApplicationProvider
import com.neohear.data.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class SyncManagerTest {

    @Test
    fun simulate_sync_marks_pending_as_synced() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<android.app.Application>()
        val mgr = SyncManager.getInstance(ctx)

        // ensure a clean DB (fallbackToDestructiveMigration is configured in AppDatabase)
        val db = AppDatabase.getInstance(ctx)

        // Add a pending record
        mgr.addPending("entity-1", "TestSession", false)
        var pending = mgr.getPendingCount()
        assertTrue(pending >= 1)

        val processed = mgr.simulateSync(true)
        assertTrue(processed >= 1)

        pending = mgr.getPendingCount()
        assertEquals(0, pending)
    }
}
