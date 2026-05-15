package com.kaushalya.karnataka.ui.screens.aibuilder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.AiBioUiState
import com.kaushalya.karnataka.viewmodel.AiBioViewModel
import kotlinx.coroutines.delay

private val STEPS = listOf(
    "What is your full name?"                   to "e.g. Raju Nayak",
    "What is your skill or trade?"              to "e.g. Electrician, Plumber",
    "How many years of experience do you have?" to "e.g. 8 years",
    "What are you best known for?"              to "e.g. fan installation, wiring",
)

@Composable
fun AiBuilderScreen(viewModel: AiBioViewModel = hiltViewModel()) {
    val uiState       by viewModel.uiState.collectAsState()
    val editableBio   by viewModel.editableBio.collectAsState()
    val savedSuccess  by viewModel.savedSuccessfully.collectAsState()

    var step          by remember { mutableIntStateOf(0) }
    var currentInput  by remember { mutableStateOf("") }
    val answers       = remember { mutableStateListOf("", "", "", "") }
    var displayedBio  by remember { mutableStateOf("") }
    val snackbarHost  = remember { SnackbarHostState() }

    // Typewriter effect
    LaunchedEffect(uiState) {
        if (uiState is AiBioUiState.Success) {
            val bio = (uiState as AiBioUiState.Success).bio
            displayedBio = ""
            bio.forEachIndexed { _, ch ->
                displayedBio += ch
                delay(18)
            }
        }
    }

    LaunchedEffect(savedSuccess) {
        if (savedSuccess) snackbarHost.showSnackbar("✅ Bio saved to your profile!")
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Header ────────────────────────────────────────────────
            Surface(color = RoyalBlue, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("🤖 AI Profile Builder", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Answer 4 questions. Gemini writes your professional bio.", color = PureWhite.copy(0.8f), fontSize = 12.sp)
                }
            }

            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

                // ── Show questionnaire only before generating ─────────
                if (uiState !is AiBioUiState.Success) {
                    // Progress bar
                    val progress = step / STEPS.size.toFloat()
                    LinearProgressIndicator(
                        progress    = { progress },
                        modifier    = Modifier.fillMaxWidth(),
                        color       = SaffronOrange,
                        trackColor  = SaffronOrange.copy(alpha = 0.2f),
                    )
                    Text("Step ${step + 1} of ${STEPS.size}", fontSize = 11.sp, color = DarkGrey)

                    // Question
                    Text(
                        text       = "Q${step + 1}: ${STEPS[step].first}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = RoyalBlue,
                    )
                    OutlinedTextField(
                        value         = currentInput,
                        onValueChange = { currentInput = it },
                        placeholder   = { Text(STEPS[step].second, color = DarkGrey) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = false,
                        minLines      = 2,
                        shape         = RoundedCornerShape(12.dp),
                    )

                    // Answered steps summary
                    answers.take(step).forEachIndexed { i, ans ->
                        if (ans.isNotBlank()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Q${i + 1}:", fontWeight = FontWeight.Bold, color = DarkGrey, fontSize = 12.sp)
                                Text(ans, color = DarkGrey, fontSize = 12.sp)
                            }
                        }
                    }

                    // Next / Generate button
                    val isLast = step == STEPS.size - 1
                    Button(
                        onClick = {
                            if (currentInput.isBlank()) return@Button
                            answers[step] = currentInput
                            if (isLast) {
                                viewModel.name.value       = answers[0]
                                viewModel.skill.value      = answers[1]
                                viewModel.experience.value = answers[2]
                                viewModel.speciality.value = answers[3]
                                viewModel.location.value   = answers[0] // reuse name field location fallback
                                viewModel.generateBio()
                            } else {
                                step++
                                currentInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(40.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (isLast) SaffronOrange else ElectricBlue
                        ),
                        enabled  = uiState !is AiBioUiState.Loading,
                    ) {
                        if (uiState is AiBioUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PureWhite, strokeWidth = 2.dp)
                        } else {
                            Text(if (isLast) "✨ Generate Bio" else "Next →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    if (step > 0) {
                        TextButton(onClick = { step--; currentInput = answers.getOrElse(step - 1) { "" } }) {
                            Text("← Back", color = DarkGrey)
                        }
                    }
                }

                // ── Generated bio result ──────────────────────────────
                AnimatedVisibility(visible = uiState is AiBioUiState.Success) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("✦ Your AI-Generated Bio", fontWeight = FontWeight.Bold, color = RoyalBlue, fontSize = 15.sp)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElectricBlue.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(displayedBio, fontSize = 14.sp, color = NearBlack, lineHeight = 22.sp)
                        }

                        // Editable version
                        OutlinedTextField(
                            value         = viewModel.editableBio.collectAsState().value,
                            onValueChange = { viewModel.editableBio.value = it },
                            label         = { Text("Edit your bio") },
                            modifier      = Modifier.fillMaxWidth(),
                            minLines      = 3,
                            shape         = RoundedCornerShape(12.dp),
                        )

                        Button(
                            onClick  = { viewModel.saveToProfile() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape    = RoundedCornerShape(40.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        ) {
                            Text("Save to Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedButton(
                            onClick  = {
                                step = 0; currentInput = ""; answers.fill(""); viewModel.uiState.value = AiBioUiState.Idle
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(40.dp),
                        ) {
                            Text("Start Over", color = DarkGrey)
                        }
                    }
                }

                // ── Error state ───────────────────────────────────────
                if (uiState is AiBioUiState.Error) {
                    Card(colors = CardDefaults.cardColors(containerColor = CrimsonRed.copy(alpha = 0.1f))) {
                        Text(
                            text     = "Error: ${(uiState as AiBioUiState.Error).message}",
                            color    = CrimsonRed,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}
