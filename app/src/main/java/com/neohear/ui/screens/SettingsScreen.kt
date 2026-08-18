package com.neohear.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.neohear.demo.DemoScenarioRegistry
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neohear.ui.theme.NeoHearTheme

@Composable
fun SettingsScreen(
    isDemoMode: Boolean,
    onDemoModeToggle: (Boolean) -> Unit,
    onRunDemoScenario: (String) -> Unit = {},
    connectivityState: com.neohear.sync.ConnectivityState = com.neohear.sync.ConnectivityState.OFFLINE,
    pendingSyncCount: Int = 0,
    totalSyncRecords: Int = 0,
    onSimulateSync: () -> Unit = {},
    onSimulateFail: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isDemoMode)
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Demo Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Use reference waveforms instead of live probe",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Switch(
                    checked = isDemoMode,
                    onCheckedChange = onDemoModeToggle
                )
            }

            if (isDemoMode) {
                Text(
                    text = "ACTIVE — Screenings will use pre-recorded OAE waveforms. " +
                            "No probe required. Results are synthetic and for demonstration only.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Demo Scenarios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DemoScenarioRegistry.scenarios.forEach { scenario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = scenario.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = scenario.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { onRunDemoScenario(scenario.id) }) { Text("Run") }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Data & Sync", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = when (connectivityState) {
                        com.neohear.sync.ConnectivityState.ONLINE -> "✓ Online"
                        com.neohear.sync.ConnectivityState.SYNCING -> "⟳ Syncing"
                        com.neohear.sync.ConnectivityState.OFFLINE -> "⚠ Offline — Data stored locally"
                    }, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Local Storage: ✓ Encrypted", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Pending Sync: $pendingSyncCount records", fontSize = 13.sp)
                    Text(text = "Total Sync Records: $totalSyncRecords", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onSimulateSync) { Text("Simulate Sync") }
                        Button(onClick = onSimulateFail) { Text("Simulate Failure") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Simulation only — no server connection.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "About",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NeoHear v1.0 — Newborn Hearing Screening App (Hackathon Prototype)\n" +
                            "DSP Pipeline: Prompt 3 (StimulusGenerator, SignalAverager, SnrClassifier)\n" +
                            "Risk Factors: JCIH 2019 guidelines\n" +
                            "PLACEHOLDER thresholds — NOT clinically validated\n" +
                            "This app is NOT a certified medical device.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    NeoHearTheme {
        SettingsScreen(isDemoMode = true, onDemoModeToggle = {})
    }
}
