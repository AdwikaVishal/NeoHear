package com.neohear.ui.questionnaire

import android.speech.tts.TextToSpeech
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neohear.data.entity.RiskLevel
import com.neohear.ui.theme.NeoHearTheme

/**
 * Main screen for the risk-factor questionnaire flow.
 *
 * Presents one question at a time with Yes/No icon-led buttons,
 * voice-prompted via Android TextToSpeech.
 *
 * On completion, shows the risk level result with appropriate warnings.
 *
 * @param viewModel The [RiskQuestionnaireViewModel] driving the flow.
 * @param onNavigateBack Called when the user presses the back/close button.
 */
@Composable
fun RiskQuestionnaireScreen(
    viewModel: RiskQuestionnaireViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isComplete) {
        RiskResultScreen(
            riskLevel = state.riskLevel,
            isSaving = state.isSaving,
            onSave = { viewModel.saveResponse() },
            onReset = { viewModel.reset() },
            onNavigateBack = onNavigateBack
        )
    } else {
        QuestionScreen(
            factor = viewModel.currentFactor,
            questionNumber = state.currentIndex + 1,
            totalQuestions = viewModel.totalQuestions,
            progress = viewModel.progress,
            onAnswer = { viewModel.answerCurrent(it) },
            onBack = onNavigateBack
        )
    }
}

/**
 * Single-question screen with voice prompt and Yes/No icon buttons.
 */
@VisibleForTesting
@Composable
internal fun QuestionScreen(
    factor: RiskFactor,
    questionNumber: Int,
    totalQuestions: Int,
    progress: Float,
    onAnswer: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // TextToSpeech setup
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Language will be set; tts instance is returned by remember
            }
        }
    }

    // Set language after TTS is initialized
    LaunchedEffect(tts) {
        tts.language = java.util.Locale.getDefault()
    }

    // Speak the question when it changes
    LaunchedEffect(factor.id) {
        @Suppress("DEPRECATION")
        tts.speak(factor.question, TextToSpeech.QUEUE_FLUSH, null, "question_${factor.id}")
    }

    // Cleanup TTS on dispose
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: back button + progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("Back")
                }
                Text(
                    text = "$questionNumber / $totalQuestions",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Question card
            AnimatedContent(
                targetState = factor,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "question_transition"
            ) { currentFactor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Severity badge
                        Text(
                            text = if (currentFactor.severity == RiskFactorSeverity.MAJOR)
                                "Major Risk Factor" else "Minor Risk Factor",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentFactor.severity == RiskFactorSeverity.MAJOR)
                                MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = currentFactor.question,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Yes/No buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // NO button
                OutlinedButton(
                    onClick = { onAnswer("NO") },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "No",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // YES button
                Button(
                    onClick = { onAnswer("YES") },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Yes",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Yes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Result screen shown after completing all questions.
 *
 * Displays the computed risk level with a clear disclaimer that this is NOT a hearing test.
 */
@Composable
fun RiskResultScreen(
    riskLevel: RiskLevel?,
    isSaving: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (riskLevel) {
                RiskLevel.HIGH -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "High Risk",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "HIGH RISK",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                RiskLevel.ELEVATED -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Elevated Risk",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "ELEVATED RISK",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                RiskLevel.LOW -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Low Risk",
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "LOW RISK",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                null -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = riskLevelSummary(riskLevel ?: RiskLevel.LOW),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Warning box for ELEVATED/HIGH
            if (riskLevel == RiskLevel.ELEVATED || riskLevel == RiskLevel.HIGH) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Important Notice",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = QUESTIONNAIRE_WARNING_MESSAGE,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retake")
                }

                Button(
                    onClick = {
                        onSave()
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save & Finish")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text("Skip — Go Back")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionScreenPreview() {
    NeoHearTheme {
        QuestionScreen(
            factor = RiskFactorCatalog.factors[0],
            questionNumber = 1,
            totalQuestions = RiskFactorCatalog.factors.size,
            progress = 1f / RiskFactorCatalog.factors.size,
            onAnswer = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenHighRiskPreview() {
    NeoHearTheme {
        RiskResultScreen(
            riskLevel = RiskLevel.HIGH,
            isSaving = false,
            onSave = {},
            onReset = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenLowRiskPreview() {
    NeoHearTheme {
        RiskResultScreen(
            riskLevel = RiskLevel.LOW,
            isSaving = false,
            onSave = {},
            onReset = {},
            onNavigateBack = {}
        )
    }
}
