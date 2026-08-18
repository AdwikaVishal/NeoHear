package com.neohear.ui.referrals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neohear.data.entity.ReferralStatus
import com.neohear.reminder.SimulatedSmsLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralDetailScreen(
    viewModel: ReferralsViewModel,
    onBack: () -> Unit = {}
) {
    val state by viewModel.detailState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Referral Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is ReferralDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ReferralDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s.message,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ReferralDetailState.Loaded -> {
                ReferralDetailContent(
                    state = s,
                    onStatusUpdate = { newStatus ->
                        viewModel.updateStatus(s.referral.id, newStatus)
                    },
                    onAddNote = { note ->
                        viewModel.addFollowUpNote(s.referral.id, note)
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ReferralDetailContent(
    state: ReferralDetailState.Loaded,
    onStatusUpdate: (ReferralStatus) -> Unit,
    onAddNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.US)
    val dayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val referral = state.referral

    var showStatusDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    // Compute next statuses outside the Column scope so dialogs can reference it
    val nextStatuses = when (referral.status) {
        ReferralStatus.PENDING -> listOf(ReferralStatus.SCHEDULED, ReferralStatus.COMPLETED, ReferralStatus.LOST_TO_FOLLOW_UP)
        ReferralStatus.SCHEDULED -> listOf(ReferralStatus.COMPLETED, ReferralStatus.LOST_TO_FOLLOW_UP)
        ReferralStatus.COMPLETED -> emptyList()
        ReferralStatus.LOST_TO_FOLLOW_UP -> emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Patient info
        Text(
            text = state.patientName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Patient ID: ${referral.patientId.toString().take(12)}…",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Referral info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Ear", state.ear?.name ?: "Unknown")
                DetailRow("Created", dateFormat.format(Date(referral.createdAt)))
                DetailRow("Last Updated", dateFormat.format(Date(referral.updatedAt)))
                DetailRow("Status", referral.status.name.replace("_", " "))
                DetailRow("Follow-up Notes", "${referral.followUpLog.size}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Text(
            text = "Actions",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (nextStatuses.isNotEmpty()) {
            OutlinedButton(
                onClick = { showStatusDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Status")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (referral.status != ReferralStatus.COMPLETED && referral.status != ReferralStatus.LOST_TO_FOLLOW_UP) {
            OutlinedButton(
                onClick = { showNoteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Follow-up Note")
            }
        }

        // Follow-up log
        if (referral.followUpLog.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Follow-up Log",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            referral.followUpLog.sortedByDescending { it.timestamp }.forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = dayDateFormat.format(Date(event.timestamp)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = event.note,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Simulated SMS log section
        val smsEntries by SimulatedSmsLog.entries.collectAsState()
        val relevantEntries = smsEntries.filter {
            it.message.contains(referral.patientId.toString().take(8))
        }

        if (relevantEntries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Simulated SMS Log",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    relevantEntries.takeLast(5).forEach { entry ->
                        Text(
                            text = "[${entry.formattedTime}] SMS logged",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = entry.message.lines().drop(1).take(2).joinToString("\n"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Status update dialog
    if (showStatusDialog && nextStatuses.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Status") },
            text = {
                Column {
                    Text(
                        text = "Current: ${referral.status.name.replace("_", " ")}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    nextStatuses.forEach { status ->
                        TextButton(
                            onClick = {
                                onStatusUpdate(status)
                                showStatusDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = status.name.replace("_", " "),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add note dialog
    if (showNoteDialog) {
        var noteText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Add Follow-up Note") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note") },
                    placeholder = { Text("e.g. Called parent, scheduled for re-test on Friday") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddNote(noteText)
                        showNoteDialog = false
                    },
                    enabled = noteText.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
