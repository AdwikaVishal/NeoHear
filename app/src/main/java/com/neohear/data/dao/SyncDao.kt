package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neohear.data.entity.SyncRecord
import com.neohear.data.entity.SyncState

@Dao
interface SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SyncRecord): Long

    @Query("SELECT COUNT(*) FROM sync_records WHERE state = :state")
    suspend fun countByState(state: SyncState): Int

    @Query("SELECT COUNT(*) FROM sync_records WHERE state = 'PENDING_SYNC'")
    suspend fun countPending(): Int

    @Query("SELECT * FROM sync_records WHERE state = 'PENDING_SYNC'")
    suspend fun getPending(): List<SyncRecord>

    @Update
    suspend fun update(record: SyncRecord)

    @Query("UPDATE sync_records SET state = :state, last_attempt_at = :attemptAt WHERE id IN (:ids)")
    suspend fun bulkUpdateState(ids: List<Long>, state: SyncState, attemptAt: Long)

    @Query("SELECT COUNT(*) FROM sync_records")
    suspend fun totalCount(): Int
}
