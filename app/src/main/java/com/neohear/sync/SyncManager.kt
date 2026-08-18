package com.neohear.sync

import android.content.Context
import com.neohear.data.AppDatabase
import com.neohear.data.entity.SyncRecord
import com.neohear.data.entity.SyncState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Lightweight manager for marking local records as pending and running a deterministic
 * simulated sync for demo purposes. This intentionally does not contact any server.
 */
class SyncManager private constructor(private val context: Context) {

    private val db by lazy { AppDatabase.getInstance(context) }

    suspend fun addPending(entityId: String, entityType: String, isDemo: Boolean) {
        val rec = SyncRecord(entityId = entityId, entityType = entityType, isDemo = isDemo, state = SyncState.PENDING_SYNC)
        db.syncDao().insert(rec)
    }

    suspend fun getPendingCount(): Int = db.syncDao().countPending()

    suspend fun getTotalSyncRecords(): Int = db.syncDao().totalCount()

    /**
     * Simulate a sync run. If `succeed` is true then all PENDING_SYNC are marked SYNCED.
     * If `succeed` is false, they're marked SYNC_FAILED. This is deterministic and local-only.
     */
    suspend fun simulateSync(succeed: Boolean = true): Int = withContext(Dispatchers.IO) {
        val pending = db.syncDao().getPending()
        if (pending.isEmpty()) return@withContext 0
        // Mark as syncing (logical) -- here we just wait a short time to simulate work
        delay(800)
        val now = System.currentTimeMillis()
        val ids = pending.map { it.id }
        val newState = if (succeed) SyncState.SYNCED else SyncState.SYNC_FAILED
        db.syncDao().bulkUpdateState(ids, newState, now)
        pending.size
    }

    companion object {
        @Volatile
        private var INSTANCE: SyncManager? = null

        fun getInstance(context: Context): SyncManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SyncManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
