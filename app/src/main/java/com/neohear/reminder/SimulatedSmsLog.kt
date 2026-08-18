package com.neohear.reminder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * In-memory log of simulated SMS messages sent by [FollowUpReminder].
 * Visible in the Referrals detail screen for demo/debug purposes.
 */
object SimulatedSmsLog {

    private val _entries = MutableStateFlow<List<SmsLogEntry>>(emptyList())
    val entries: StateFlow<List<SmsLogEntry>> = _entries.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun log(message: String) {
        val entry = SmsLogEntry(
            timestamp = System.currentTimeMillis(),
            formattedTime = dateFormat.format(Date()),
            message = message
        )
        _entries.value = _entries.value + entry
    }

    fun clear() {
        _entries.value = emptyList()
    }

    fun getAll(): List<SmsLogEntry> = _entries.value
}

data class SmsLogEntry(
    val timestamp: Long,
    val formattedTime: String,
    val message: String
)
