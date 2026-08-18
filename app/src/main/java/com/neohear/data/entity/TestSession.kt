package com.neohear.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "test_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"])]
)
data class TestSession(
    @PrimaryKey
    val id: UUID,
    val patientId: UUID,
    val ear: Ear,
    val stage: Int,
    val timestamp: Long,
    val preCheckNoiseLevel: Float,
    val preCheckSealOk: Boolean,
    val mode: Mode,
    val rawSignalRef: String?,
    val snrValue: Float?,
    val result: TestResult
)
