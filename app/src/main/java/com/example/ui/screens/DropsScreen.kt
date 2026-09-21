package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.content.DropDetail
import com.example.content.DropsEngine
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkHover
import com.example.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun DropsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val dropsEntities by viewModel.drops.collectAsState()
    val selectedDrop by viewModel.selectedDrop.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("drops_screen")
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
                    contentDescription = "Back",
                    tint = ChalkWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (selectedDrop != null) {
                                viewModel.completeCurrentDrop("") // dismiss detail
                            } else {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedDrop != null) "${selectedDrop!!.dropNumber} // ARTIFACT" else "LAUNCHER DROPS",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedDrop != null) {
                // Drop Detail & Interactive Experiment
                DropInteractiveDetail(
                    drop = selectedDrop!!,
                    onComplete = { viewModel.completeCurrentDrop(selectedDrop!!.dropNumber) }
                )
            } else {
                // List of Drops
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(DropsEngine.dropList) { drop ->
                        val entity = dropsEntities.firstOrNull { it.dropNumber == drop.dropNumber }
                        val isCompleted = entity?.isCompleted == true

                        DropCardItem(
                            drop = drop,
                            isCompleted = isCompleted,
                            onClick = { viewModel.openDrop(drop.dropNumber) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropCardItem(
    drop: DropDetail,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = drop.dropNumber,
                    style = Typography.labelSmall.copy(
                        color = ElectricChartreuse,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "// ${drop.domain}",
                    style = Typography.labelSmall.copy(color = NeutralGray)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = drop.title,
                style = Typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = drop.subtitle,
                style = Typography.bodySmall.copy(color = NeutralGray)
            )
        }

        if (isCompleted) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElectricChartreuse.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = ElectricChartreuse,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceDarkHover)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ENTER",
                    style = Typography.labelSmall.copy(
                        color = ChalkWhite,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun DropInteractiveDetail(
    drop: DropDetail,
    onComplete: () -> Unit
) {
    var timerRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var completedSuccessfully by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            secondsLeft = 30
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
            completedSuccessfully = true
            timerRunning = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = drop.title,
                style = Typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = ChalkWhite
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = drop.subtitle,
                style = Typography.labelLarge.copy(
                    color = ElectricChartreuse,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = drop.content,
                    style = Typography.bodyLarge.copy(
                        color = ChalkWhite,
                        lineHeight = 24.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "DIRECTIVE: ${drop.directive}",
                style = Typography.labelMedium.copy(
                    color = NeutralGray,
                    letterSpacing = 0.8.sp
                )
            )
        }

        // Drop 001 Stillness Experiment Ring
        if (drop.dropNumber == "DROP 001") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .clickable {
                            if (!timerRunning && !completedSuccessfully) {
                                timerRunning = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val progress = (30 - secondsLeft) / 30f
                    Canvas(modifier = Modifier.size(140.dp)) {
                        drawCircle(
                            color = SurfaceDarkHover,
                            style = Stroke(width = 8.dp.toPx())
                        )
                        drawArc(
                            color = ElectricChartreuse,
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    if (completedSuccessfully) {
                        Text(
                            text = "STILLNESS\nCOMPLETE",
                            style = Typography.labelLarge.copy(
                                color = ElectricChartreuse,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    } else if (timerRunning) {
                        Text(
                            text = "$secondsLeft",
                            style = Typography.displayMedium.copy(
                                color = ChalkWhite,
                                fontWeight = FontWeight.Black
                            )
                        )
                    } else {
                        Text(
                            text = "TAP TO\nSTART 30S",
                            style = Typography.labelLarge.copy(
                                color = ChalkWhite,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                }
            }
        }

        // Completion Action
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ElectricChartreuse)
                .clickable { onComplete() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "COMPLETE ARTIFACT",
                style = Typography.labelLarge.copy(
                    color = ObsidianBlack,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )
        }
    }
}
