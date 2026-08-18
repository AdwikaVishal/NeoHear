package com.neohear.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "referrals",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TestSession::class,
            parentColumns = ["id"],
            childColumns = ["testSessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["testSessionId"])
    ]
)
data class Referral(
    @PrimaryKey
    val id: UUID,
    val patientId: UUID,
    val testSessionId: UUID,
    val status: ReferralStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val followUpLog: List<FollowUpEvent> = emptyList()
)
