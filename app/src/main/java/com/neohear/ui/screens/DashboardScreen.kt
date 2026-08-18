package com.neohear.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neohear.data.dao.DailyTestCount
import com.neohear.ui.dashboard.DashboardUiState
import com.neohear.ui.dashboard.DashboardViewModel
import com.neohear.ui.dashboard.TimePeriod
import com.neohear.ui.screening.DemoModeBanner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, isDemoMode: Boolean = false) {
    val state by viewModel.dashboardState.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        if (isDemoMode) {
            DemoModeBanner()
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = "Dashboard",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time period selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimePeriod.entries.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { viewModel.selectPeriod(period) },
                    label = { Text(period.label, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // ── Summary stats row ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Tests",
                    value = "${state.totalTests}",
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Pass %",
                    value = "${(state.passRate * 100).toInt()}%",
                    color = Color(0xFF4CAF50)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Refer %",
                    value = "${(state.referRate * 100).toInt()}%",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Referral summary ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Referrals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ReferralStatItem(
                            label = "Pending",
                            count = state.pendingReferrals,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        ReferralStatItem(
                            label = "Resolved",
                            count = state.resolvedReferrals,
                            color = Color(0xFF4CAF50)
                        )
                        ReferralStatItem(
                            label = "Lost",
                            count = state.lostToFollowUp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Pass / Refer breakdown ────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Test Results (${selectedPeriod.label})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.totalTests == 0) {
                        Text(
                            text = "No tests in this period",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        ResultBar(
                            label = "PASS",
                            count = state.passCount,
                            total = state.totalTests,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ResultBar(
                            label = "REFER",
                            count = state.referCount,
                            total = state.totalTests,
                            color = MaterialTheme.colorScheme.error
                        )
                        if (state.repeatCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            ResultBar(
                                label = "REPEAT",
                                count = state.repeatCount,
                                total = state.totalTests,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Mode usage breakdown ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mode Usage (${selectedPeriod.label})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val totalMode = state.probeCount + state.questionnaireCount
                    if (totalMode == 0) {
                        Text(
                            text = "No activity in this period",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        ModeBar(
                            label = "Probe (OAE)",
                            count = state.probeCount,
                            total = totalMode,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ModeBar(
                            label = "Questionnaire",
                            count = state.questionnaireCount,
                            total = totalMode,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Bar chart: Pass / Refer over last 7 days ──────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pass / Refer — Last 7 Days",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.dailyCounts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No data yet",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        PassReferBarChart(
                            dailyCounts = state.dailyCounts,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }

                    // Legend
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendDot(color = Color(0xFF4CAF50))
                        Text(" PASS", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        LegendDot(color = MaterialTheme.colorScheme.error)
                        Text(" REFER", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

// ── Referral stat item ────────────────────────────────────────────

@Composable
private fun ReferralStatItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Result horizontal bar ─────────────────────────────────────────

@Composable
private fun ResultBar(label: String, count: Int, total: Int, color: Color) {
    val fraction = count.toFloat() / total
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(56.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .background(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(20.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        Text(
            text = " $count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Mode horizontal bar ───────────────────────────────────────────

@Composable
private fun ModeBar(label: String, count: Int, total: Int, color: Color) {
    val fraction = count.toFloat() / total
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .background(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(20.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        Text(
            text = " $count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Legend dot ────────────────────────────────────────────────────

@Composable
private fun LegendDot(color: Color) {
    Canvas(modifier = Modifier.size(10.dp)) {
        drawCircle(color = color)
    }
}

// ── Hand-rolled Canvas bar chart ──────────────────────────────────

@Composable
private fun PassReferBarChart(
    dailyCounts: List<DailyTestCount>,
    modifier: Modifier = Modifier
) {
    val passColor = Color(0xFF4CAF50)
    val referColor = MaterialTheme.colorScheme.error
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    val dayFormat = SimpleDateFormat("EEE", Locale.US)

    Canvas(modifier = modifier) {
        val leftPadding = 36f
        val bottomPadding = 32f
        val topPadding = 8f
        val chartWidth = size.width - leftPadding
        val chartHeight = size.height - bottomPadding - topPadding

        val maxVal = (dailyCounts.maxOfOrNull { maxOf(it.passCount, it.referCount) } ?: 0).coerceAtLeast(1)

        val barGroupWidth = chartWidth / dailyCounts.size.coerceAtLeast(1)
        val barWidth = barGroupWidth * 0.3f
        val barGap = barGroupWidth * 0.05f

        // Horizontal grid lines
        for (i in 0..3) {
            val y = topPadding + chartHeight * (1f - i / 3f)
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            // Y-axis labels
            drawContext.canvas.nativeCanvas.drawText(
                "${(maxVal * i / 3)}",
                4f,
                y + 4f,
                android.graphics.Paint().apply {
                    color = labelColor.hashCode()
                    textSize = 22f
                    isAntiAlias = true
                }
            )
        }

        // Bars
        dailyCounts.forEachIndexed { index, day ->
            val groupX = leftPadding + index * barGroupWidth + barGroupWidth * 0.15f

            // Pass bar
            val passHeight = if (maxVal > 0) (day.passCount.toFloat() / maxVal) * chartHeight else 0f
            drawRoundRect(
                color = passColor,
                topLeft = Offset(groupX, topPadding + chartHeight - passHeight),
                size = Size(barWidth, passHeight.coerceAtLeast(2f)),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Refer bar
            val referHeight = if (maxVal > 0) (day.referCount.toFloat() / maxVal) * chartHeight else 0f
            drawRoundRect(
                color = referColor,
                topLeft = Offset(groupX + barWidth + barGap, topPadding + chartHeight - referHeight),
                size = Size(barWidth, referHeight.coerceAtLeast(2f)),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // X-axis label (day of week)
            val cal = Calendar.getInstance().apply { timeInMillis = day.dayStart }
            val label = dayFormat.format(Date(day.dayStart))
            drawContext.canvas.nativeCanvas.drawText(
                label,
                groupX + barWidth,
                size.height - 6f,
                android.graphics.Paint().apply {
                    color = labelColor.hashCode()
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}
