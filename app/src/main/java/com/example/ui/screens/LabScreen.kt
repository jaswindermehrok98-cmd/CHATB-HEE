package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.content.LabEngine
import com.example.content.LabExperiment
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkHover
import com.example.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun LabScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var activeExperiment by remember { mutableStateOf<LabExperiment?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("lab_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return",
                    tint = ChalkWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (activeExperiment != null) {
                                activeExperiment = null
                            } else {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (activeExperiment != null) "${activeExperiment!!.code} // ACTIVE" else "UNPLUG PERCEPTION LAB",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (activeExperiment != null) {
                when (activeExperiment!!.id) {
                    "exp_time" -> TemporalEstimationLab()
                    "exp_reaction" -> ReactionLatencyLab()
                    else -> GenericLabExperimentView(activeExperiment!!)
                }
            } else {
                // List of Lab Experiments
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(LabEngine.experiments) { exp ->
                        LabExperimentCard(
                            experiment = exp,
                            onStart = { activeExperiment = exp }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabExperimentCard(
    experiment: LabExperiment,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { onStart() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = experiment.code,
                style = Typography.labelSmall.copy(
                    color = ElectricChartreuse,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.0.sp
                )
            )
            Text(
                text = "// ${experiment.domain}",
                style = Typography.labelSmall.copy(color = NeutralGray)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = experiment.title,
            style = Typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ChalkWhite
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = experiment.description,
            style = Typography.bodyMedium.copy(color = NeutralGray)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .align(Alignment.End)
                .clip(RoundedCornerShape(4.dp))
                .background(SurfaceDarkHover)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "RUN TEST →",
                style = Typography.labelSmall.copy(
                    color = ChalkWhite,
                    letterSpacing = 0.8.sp
                )
            )
        }
    }
}

@Composable
private fun TemporalEstimationLab() {
    var isRunning by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }
    var resultDeltaMillis by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ESTIMATE EXACTLY 10.00 SECONDS",
                style = Typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap Start, close your eyes, and tap Stop when you believe exactly 10 seconds have elapsed.",
                style = Typography.bodyMedium.copy(
                    color = NeutralGray,
                    textAlign = TextAlign.Center
                )
            )
        }

        // Test Area
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isRunning) ElectricChartreuse else SurfaceDark)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                .clickable {
                    if (!isRunning) {
                        startTime = System.currentTimeMillis()
                        resultDeltaMillis = null
                        isRunning = true
                    } else {
                        val elapsed = System.currentTimeMillis() - startTime
                        resultDeltaMillis = elapsed
                        isRunning = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isRunning) {
                Text(
                    text = "ESTIMATING...\nTAP TO STOP",
                    style = Typography.titleMedium.copy(
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                )
            } else if (resultDeltaMillis != null) {
                val elapsedSecs = resultDeltaMillis!! / 1000.0
                val deltaSecs = abs(elapsedSecs - 10.0)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.2fs".format(elapsedSecs),
                        style = Typography.displayMedium.copy(
                            color = ChalkWhite,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Text(
                        text = if (deltaSecs < 0.5) "EXCELLENT PERCEPTION" else "DELTA: %.2fs".format(deltaSecs),
                        style = Typography.labelSmall.copy(
                            color = ElectricChartreuse,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Text(
                    text = "TAP TO START",
                    style = Typography.titleMedium.copy(
                        color = ChalkWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Text(
            text = "Digital screen addiction accelerates subjective time perception.",
            style = Typography.labelSmall.copy(
                color = NeutralGray,
                letterSpacing = 0.8.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun ReactionLatencyLab() {
    var state by remember { mutableStateOf("WAIT") } // WAIT, READY, CLICK, RESULT, EARLY
    var reactionTime by remember { mutableLongStateOf(0L) }
    var signalStart by remember { mutableLongStateOf(0L) }

    LaunchedEffect(state) {
        if (state == "READY") {
            val waitTime = Random.nextLong(2000, 5000)
            delay(waitTime)
            if (state == "READY") {
                signalStart = System.currentTimeMillis()
                state = "CLICK"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ATTENTION REFLEX TEST",
                style = Typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "When the black box turns electric green, tap as fast as possible.",
                style = Typography.bodyMedium.copy(color = NeutralGray)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (state == "CLICK") ElectricChartreuse else SurfaceDark)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                .clickable {
                    when (state) {
                        "WAIT", "RESULT", "EARLY" -> state = "READY"
                        "READY" -> state = "EARLY"
                        "CLICK" -> {
                            reactionTime = System.currentTimeMillis() - signalStart
                            state = "RESULT"
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                "WAIT" -> Text("TAP TO ARM TEST", style = Typography.titleMedium.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                "READY" -> Text("WAIT FOR GREEN...", style = Typography.titleMedium.copy(color = NeutralGray))
                "CLICK" -> Text("TAP NOW!", style = Typography.displayMedium.copy(color = ObsidianBlack, fontWeight = FontWeight.Black))
                "EARLY" -> Text("TOO EARLY!\nTAP TO RETRY", style = Typography.titleMedium.copy(color = AlertRed, textAlign = TextAlign.Center))
                "RESULT" -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${reactionTime} ms", style = Typography.displayLarge.copy(color = ChalkWhite, fontWeight = FontWeight.Black))
                    Text("TAP TO TEST AGAIN", style = Typography.labelSmall.copy(color = ElectricChartreuse))
                }
            }
        }

        Text(
            text = "Healthy baseline latency is between 200ms and 280ms.",
            style = Typography.labelSmall.copy(color = NeutralGray)
        )
    }
}

@Composable
private fun GenericLabExperimentView(exp: LabExperiment) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = exp.title,
                style = Typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = ChalkWhite
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "DOMAIN: ${exp.domain}",
                style = Typography.labelLarge.copy(color = ElectricChartreuse)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = exp.description,
                    style = Typography.bodyLarge.copy(color = ChalkWhite)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ElectricChartreuse)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CALIBRATION COMPLETE",
                style = Typography.labelLarge.copy(
                    color = ObsidianBlack,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
