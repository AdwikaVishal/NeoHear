package com.neohear.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.neohear.ui.cry.CryAnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryAnalysisScreen(
    onBack: () -> Unit,
    viewModel: CryAnalysisViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val context = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }

    var showNewPatientForm by remember { mutableStateOf(false) }
    var newPatientName by remember { mutableStateOf("") }
    var selectedPatientId by remember { mutableStateOf<java.util.UUID?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cry Analysis (Experimental)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Non-dismissible experimental disclaimer
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "EXPERIMENTAL / RESEARCH ONLY\n\n" +
                                "This is NOT a diagnostic tool. Results are unproven and for " +
                                "informational use only. It does NOT influence PASS/REFER screening results.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (val s = state) {
                is CryAnalysisViewModel.UiState.Idle -> {
                    // Patient selection
                    Text(
                        text = "Select Patient",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (patients.isNotEmpty()) {
                        patients.forEach { patient ->
                            val isSelected = selectedPatientId == patient.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedPatientId = patient.id
                                    viewModel.selectPatient(patient.id)
                                    showNewPatientForm = false
                                },
                                label = { Text(patient.displayNameOrCode) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    } else {
                        Text(
                            text = "No patients yet. Create one below.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!showNewPatientForm) {
                        OutlinedButton(
                            onClick = { showNewPatientForm = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create New Patient")
                        }
                    } else {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = newPatientName,
                                    onValueChange = { newPatientName = it },
                                    label = { Text("Baby name or ID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(onClick = {
                                        showNewPatientForm = false
                                        newPatientName = ""
                                    }) { Text("Cancel") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (newPatientName.isNotBlank()) {
                                                viewModel.createPatient(
                                                    newPatientName.trim(),
                                                    System.currentTimeMillis()
                                                )
                                                showNewPatientForm = false
                                                newPatientName = ""
                                            }
                                        }
                                    ) { Text("Create") }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Record button
                    Button(
                        onClick = {
                            if (!hasMicPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                return@Button
                            }
                            if (selectedPatientId == null && !showNewPatientForm) {
                                // If there are patients but none selected, select the first
                                if (patients.isNotEmpty()) {
                                    selectedPatientId = patients.first().id
                                    viewModel.selectPatient(patients.first().id)
                                }
                            }
                            viewModel.startAnalysis()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Start Recording (~5 seconds)", fontSize = 16.sp)
                    }

                    if (!hasMicPermission) {
                        Text(
                            text = "Microphone permission is required for cry analysis.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Place the phone near the baby and tap to record ~5 seconds of cry audio.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }

                is CryAnalysisViewModel.UiState.Recording -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Recording... Please ensure the baby is crying.", fontSize = 16.sp)
                }

                is CryAnalysisViewModel.UiState.Analyzing -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing acoustic features...", fontSize = 16.sp)
                }

                is CryAnalysisViewModel.UiState.Result -> {
                    val res = s.data
                    Text(
                        text = "Analysis Complete",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Acoustic Features", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            FeatureRow("Avg Pitch", "${"%.1f".format(res.avgPitchHz)} Hz")
                            FeatureRow("Pitch Std Dev", "${"%.1f".format(res.pitchStdDev)}")
                            FeatureRow("Avg Energy", "${"%.1f".format(res.avgEnergyDb)} dB")
                            FeatureRow("Jitter", "${"%.4f".format(res.jitter)}")
                            FeatureRow("Shimmer", "${"%.4f".format(res.shimmer)}")
                            FeatureRow("Voicing Ratio", "${"%.2f".format(res.voicingRatio)}")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val flags = res.riskFlags
                    if (flags > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Risk Flags Detected (Experimental):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (flags and 1 != 0)
                                    Text("- High Pitch (>450 Hz)", fontSize = 14.sp)
                                if (flags and 2 != 0)
                                    Text("- Weak Energy (Quiet Cry)", fontSize = 14.sp)
                                if (flags and 4 != 0)
                                    Text("- High Jitter (Irregular Rhythm)", fontSize = 14.sp)
                                if (flags and 8 != 0)
                                    Text("- Low Voicing (Fussy, not crying)", fontSize = 14.sp)
                            }
                        }
                    } else {
                        Text(
                            "No experimental risk flags detected.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Run Another Analysis")
                    }
                }

                is CryAnalysisViewModel.UiState.Error -> {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
