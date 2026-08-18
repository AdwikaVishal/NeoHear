package com.neohear.ui.referrals

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neohear.data.entity.ReferralStatus
import com.neohear.ui.screening.DemoModeBanner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReferralsListScreen(
    viewModel: ReferralsViewModel,
    isDemoMode: Boolean = false,
    onReferralClick: (String) -> Unit = {},
    onLogFollowUp: (String) -> Unit = {}
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        floatingActionButton = {
            // FAB could be used to manually create referrals; placeholder for now
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (isDemoMode) {
                DemoModeBanner()
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "Referrals",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (!state.isLoading && state.referrals.isNotEmpty()) {
                Text(
                    text = "${state.referrals.size} total referral(s)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.referrals.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No referrals yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Referrals are created automatically when\na screening result is REFER.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.referrals, key = { it.referral.id.toString() }) { item ->
                            ReferralCard(
                                item = item,
                                onClick = { onReferralClick(item.referral.id.toString()) },
                                onLogFollowUp = { onLogFollowUp(item.referral.id.toString()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferralCard(
    item: ReferralListItem,
    onClick: () -> Unit,
    onLogFollowUp: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val referral = item.referral

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.patientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "ID: ${item.referral.patientId.toString().take(8)}…",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(status = referral.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ear: ${item.ear?.name ?: "?"}  •  Created: ${dateFormat.format(Date(referral.createdAt))}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (referral.followUpLog.isNotEmpty()) {
                        Text(
                            text = "${referral.followUpLog.size} follow-up note(s)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (referral.status != ReferralStatus.COMPLETED && referral.status != ReferralStatus.LOST_TO_FOLLOW_UP) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.TextButton(
                    onClick = onLogFollowUp,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log follow-up", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: ReferralStatus) {
    val (label, color) = when (status) {
        ReferralStatus.PENDING -> "Pending" to MaterialTheme.colorScheme.tertiary
        ReferralStatus.SCHEDULED -> "Scheduled" to MaterialTheme.colorScheme.primary
        ReferralStatus.COMPLETED -> "Completed" to MaterialTheme.colorScheme.secondary
        ReferralStatus.LOST_TO_FOLLOW_UP -> "Lost" to MaterialTheme.colorScheme.error
    }

    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        modifier = Modifier
            .padding(start = 8.dp)
    )
}
