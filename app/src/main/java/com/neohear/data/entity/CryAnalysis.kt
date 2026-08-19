package com.neohear.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "cry_analyses",
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
data class CryAnalysis(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val patientId: UUID,
    val timestamp: Long = System.currentTimeMillis(),
    val avgPitchHz: Float,
    val pitchStdDev: Float,
    val avgEnergyDb: Float,
    val jitter: Float,
    val shimmer: Float,
    val voicingRatio: Float,
    val riskFlags: Int,
    val isExperimental: Boolean = true
)
