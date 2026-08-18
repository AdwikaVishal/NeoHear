package com.neohear.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey
    val id: UUID,
    val displayNameOrCode: String,
    val dob: Date,
    val sex: String?,
    val createdAt: Long = System.currentTimeMillis()
)
