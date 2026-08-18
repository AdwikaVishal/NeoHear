package com.neohear.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neohear.data.entity.Ear
import com.neohear.data.entity.FollowUpEvent
import com.neohear.data.entity.Mode
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.RiskLevel
import com.neohear.data.entity.TestResult
import java.util.Date
import java.util.UUID

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromUuid(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun toUuid(value: String?): UUID? = value?.let { UUID.fromString(it) }

    @TypeConverter
    fun fromDate(value: Date?): Long? = value?.time

    @TypeConverter
    fun toDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun fromEar(value: Ear?): String? = value?.name

    @TypeConverter
    fun toEar(value: String?): Ear? = value?.let { Ear.valueOf(it) }

    @TypeConverter
    fun fromMode(value: Mode?): String? = value?.name

    @TypeConverter
    fun toMode(value: String?): Mode? = value?.let { Mode.valueOf(it) }

    @TypeConverter
    fun fromTestResult(value: TestResult?): String? = value?.name

    @TypeConverter
    fun toTestResult(value: String?): TestResult? = value?.let { TestResult.valueOf(it) }

    @TypeConverter
    fun fromReferralStatus(value: ReferralStatus?): String? = value?.name

    @TypeConverter
    fun toReferralStatus(value: String?): ReferralStatus? = value?.let { ReferralStatus.valueOf(it) }

    @TypeConverter
    fun fromRiskLevel(value: RiskLevel?): String? = value?.name

    @TypeConverter
    fun toRiskLevel(value: String?): RiskLevel? = value?.let { RiskLevel.valueOf(it) }

    @TypeConverter
    fun fromFollowUpEvents(value: List<FollowUpEvent>?): String? = gson.toJson(value)

    @TypeConverter
    fun toFollowUpEvents(value: String?): List<FollowUpEvent>? {
        if (value.isNullOrEmpty()) return null
        val type = object : TypeToken<List<FollowUpEvent>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? = gson.toJson(value)

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        if (value.isNullOrEmpty()) return null
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, type)
    }
}
