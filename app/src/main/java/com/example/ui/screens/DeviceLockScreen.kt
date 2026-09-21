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
import androidx.compose.material.icons.filled.Lock
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
import com.example.ui.theme.SurfaceDarkHover
import com.example.ui.theme.Typography

@Composable
fun DeviceLockScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.deviceTimerState.collectAsState()
    val isAdminActive = viewModel.deviceLockManager.isDeviceAdminActive()
    var selectedMinutes by remember { mutableFloatStateOf(30f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("device_lock_screen")
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
                    text = "DEVICE LOCK ENGINE",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Device Admin authorization card
            if (!isAdminActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, AlertRed, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "DEVICE ADMIN PERMISSION REQUIRED",
                            style = Typography.titleMedium.copy(
                                color = AlertRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To physically turn off and lock the screen when your countdown timer expires, Android requires Device Admin authorization.",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricChartreuse)
                                .clickable {
                                    val intent = viewModel.deviceLockManager.getDeviceAdminIntent()
                                    viewModel.startActivitySafely(intent)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("grant_device_admin_btn")
                        ) {
                            Text(
                                text = "ENABLE DEVICE LOCK PRIVILEGE",
                                style = Typography.labelSmall.copy(
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (timerState.isActive) {
                // Active Countdown State
                val mins = timerState.remainingSeconds / 60
                val secs = timerState.remainingSeconds % 60

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "LOCK IN PROGRESS",
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
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Phone will lock automatically upon reaching 00:00.",
                        style = Typography.bodyMedium.copy(color = NeutralGray)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceDark)
                                .border(1.dp, AlertRed, RoundedCornerShape(6.dp))
                                .clickable { viewModel.deviceLockManager.cancelTimer() }
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                                .testTag("cancel_lock_timer_btn")
                        ) {
                            Text(
                                text = "CANCEL TIMER",
                                style = Typography.labelSmall.copy(
                                    color = AlertRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricChartreuse)
                                .clickable { viewModel.deviceLockManager.lockNow() }
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                                .testTag("lock_now_immediate_btn")
                        ) {
                            Text(
                                text = "LOCK NOW",
                                style = Typography.labelSmall.copy(
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            } else {
                // Configure timer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "LOCK PHONE AFTER DURATION",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.2.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetLockChip(
                            label = "15 MIN",
                            isSelected = selectedMinutes == 15f,
                            onClick = { selectedMinutes = 15f },
                            modifier = Modifier.weight(1f)
                        )
                        PresetLockChip(
                            label = "30 MIN",
                            isSelected = selectedMinutes == 30f,
                            onClick = { selectedMinutes = 30f },
                            modifier = Modifier.weight(1f)
                        )
                        PresetLockChip(
                            label = "45 MIN",
                            isSelected = selectedMinutes == 45f,
                            onClick = { selectedMinutes = 45f },
                            modifier = Modifier.weight(1f)
                        )
                        PresetLockChip(
                            label = "60 MIN",
                            isSelected = selectedMinutes == 60f,
                            onClick = { selectedMinutes = 60f },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Instant Lock Test
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.deviceLockManager.lockNow() }
                            .padding(16.dp)
                            .testTag("instant_lock_tile"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = ElectricChartreuse,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "TEST IMMEDIATE LOCK NOW",
                                style = Typography.labelMedium.copy(
                                    color = ChalkWhite,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.0.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Start Timer Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricChartreuse)
                            .clickable {
                                viewModel.deviceLockManager.startTimer(selectedMinutes.toInt())
                                viewModel.navigateTo(Screen.HOME)
                            }
                            .padding(vertical = 16.dp)
                            .testTag("start_lock_timer_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "START ${selectedMinutes.toInt()} MINUTE LOCK COUNTDOWN",
                            style = Typography.labelLarge.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetLockChip(
    label: String,
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
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ObsidianBlack else ChalkWhite
            )
        )
    }
}
