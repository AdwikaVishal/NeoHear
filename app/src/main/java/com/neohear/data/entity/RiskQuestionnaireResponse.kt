package com.neohear.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "risk_questionnaire_responses",
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
data class RiskQuestionnaireResponse(
    @PrimaryKey
    val id: UUID,
    val patientId: UUID,
    val timestamp: Long,
    val answers: Map<String, String>,
    val riskLevel: RiskLevel
)
