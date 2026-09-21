package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun FocusScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val focusState by viewModel.focusState.collectAsState()
    var selectedMinutes by remember { mutableFloatStateOf(25f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("focus_screen")
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
                    contentDescription = "Return Home",
                    tint = ChalkWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { viewModel.navigateTo(Screen.HOME) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FOCUS ENGINE",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (focusState.isActive) {
                // Active Focus countdown view
                val mins = focusState.remainingSeconds / 60
                val secs = focusState.remainingSeconds % 60

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "FOCUS IN PROGRESS",
                        style = Typography.labelLarge.copy(
                            color = ElectricChartreuse,
                            letterSpacing = 2.0.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "%02d:%02d".format(mins, secs),
                        style = Typography.displayLarge.copy(
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2.0).sp,
                            color = ChalkWhite
                        ),
                        modifier = Modifier.testTag("focus_countdown_text")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "The urge to check will pass in twenty seconds.",
                        style = Typography.bodyMedium.copy(color = NeutralGray)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceDark)
                            .border(1.dp, AlertRed, RoundedCornerShape(6.dp))
                            .clickable { viewModel.focusSessionManager.endFocus() }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .testTag("end_focus_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "END FOCUS SESSION",
                            style = Typography.labelSmall.copy(
                                color = AlertRed,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                        )
                    }
                }
            } else {
                // Configure & Start View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "SELECT DURATION",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.2.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FocusPresetChip(
                            label = "25 MIN",
                            minutes = 25f,
                            isSelected = selectedMinutes == 25f,
                            onClick = { selectedMinutes = 25f },
                            modifier = Modifier.weight(1f)
                        )
                        FocusPresetChip(
                            label = "45 MIN",
                            minutes = 45f,
                            isSelected = selectedMinutes == 45f,
                            onClick = { selectedMinutes = 45f },
                            modifier = Modifier.weight(1f)
                        )
                        FocusPresetChip(
                            label = "60 MIN",
                            minutes = 60f,
                            isSelected = selectedMinutes == 60f,
                            onClick = { selectedMinutes = 60f },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Custom Slider
                    Text(
                        text = "CUSTOM: ${selectedMinutes.toInt()} MINUTES",
                        style = Typography.labelMedium.copy(
                            color = ElectricChartreuse,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Slider(
                        value = selectedMinutes,
                        onValueChange = { selectedMinutes = it },
                        valueRange = 5f..120f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricChartreuse,
                            activeTrackColor = ElectricChartreuse,
                            inactiveTrackColor = SurfaceBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Philosophy note
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "WHAT HAPPENS DURING FOCUS?",
                                style = Typography.labelSmall.copy(
                                    color = NeutralGray,
                                    letterSpacing = 1.0.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "The launcher simplifies into an austere single-task interface. The visual clutter disappears, giving your cognitive buffer uninterrupted silence.",
                                style = Typography.bodyMedium.copy(color = ChalkWhite)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Start Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricChartreuse)
                            .clickable {
                                viewModel.focusSessionManager.startFocus(selectedMinutes.toInt())
                                viewModel.navigateTo(Screen.HOME)
                            }
                            .padding(vertical = 16.dp)
                            .testTag("start_focus_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "START ${selectedMinutes.toInt()} MINUTE FOCUS",
                            style = Typography.labelLarge.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusPresetChip(
    label: String,
    minutes: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) ElectricChartreuse else SurfaceDark)
            .border(1.dp, if (isSelected) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ObsidianBlack else ChalkWhite,
                letterSpacing = 0.8.sp
            )
        )
    }
}
