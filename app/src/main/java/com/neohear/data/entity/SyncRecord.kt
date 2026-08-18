package com.neohear.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks synchronization state for an app-level entity.
 * This is intentionally very small and designed for demo/simulation only.
 */
@Entity(tableName = "sync_records")
data class SyncRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entity_id")
    val entityId: String,
    @ColumnInfo(name = "entity_type")
    val entityType: String,
    @ColumnInfo(name = "is_demo")
    val isDemo: Boolean = false,
    @ColumnInfo(name = "state")
    val state: SyncState = SyncState.PENDING_SYNC,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_attempt_at")
    val lastAttemptAt: Long? = null
)
