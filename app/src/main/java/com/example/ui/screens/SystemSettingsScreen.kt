package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limits.UnplugAccessibilityService
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
fun SystemSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reducedMotion by viewModel.reducedMotion.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val isPremiumUnlocked by viewModel.isPremiumUnlocked.collectAsState()
    val timeOffsetMs by viewModel.timeOffsetMs.collectAsState()

    val hasNotif = viewModel.notificationRepository.isNotificationAccessGranted()
    val hasUsage = viewModel.usageStatsHelper.hasUsagePermission()
    val hasAdmin = viewModel.deviceLockManager.isDeviceAdminActive()
    val hasAccess = UnplugAccessibilityService.isRunning()
    val isHome = viewModel.isHomeApp()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("system_settings_screen")
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
                        .clickable { viewModel.navigateTo(Screen.HOME) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SYSTEM CONFIGURATION",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 0. Sovereign Pro Suite Status
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, if (isPremiumUnlocked) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.navigateTo(Screen.PREMIUM) }
                        .padding(16.dp)
                        .testTag("system_pro_tile")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "SOVEREIGN PRO SUITE",
                                    style = Typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPremiumUnlocked) ElectricChartreuse else ChalkWhite
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isPremiumUnlocked) ElectricChartreuse.copy(alpha = 0.2f) else SurfaceDarkHover)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isPremiumUnlocked) "UNLOCKED (CODE 2013)" else "LOCKED",
                                        style = Typography.labelSmall.copy(
                                            color = if (isPremiumUnlocked) ElectricChartreuse else NeutralGray,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPremiumUnlocked) "Access 432Hz generator, stealth vault, and telemetry controls." else "Enter access code 2013 to unlock lifetime sovereign features.",
                                style = Typography.bodyMedium.copy(color = NeutralGray)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isPremiumUnlocked) SurfaceDarkHover else ElectricChartreuse)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isPremiumUnlocked) "MANAGE" else "ENTER CODE",
                                style = Typography.labelSmall.copy(
                                    color = if (isPremiumUnlocked) ElectricChartreuse else ObsidianBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // 1. Home App Launcher Role
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, if (isHome) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DEFAULT HOME LAUNCHER",
                                style = Typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ChalkWhite
                                )
                            )

                            if (isHome) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricChartreuse.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE DEFAULT",
                                        style = Typography.labelSmall.copy(
                                            color = ElectricChartreuse,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Set UNPLUG OFFLINE OS as your primary home screen replacement.",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricChartreuse)
                                .clickable {
                                    val intent = viewModel.getHomeRoleIntent()
                                    viewModel.startActivitySafely(intent)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("set_home_role_btn")
                        ) {
                            Text(
                                text = "SELECT DEFAULT HOME APP",
                                style = Typography.labelSmall.copy(
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // 2. Permissions Status Dashboard
                Text(
                    text = "DEVICE PERMISSION STATES",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.2.sp
                    )
                )

                PermissionStatusTile(
                    title = "NOTIFICATION ACCESS",
                    description = "Required for direct quick replies and triage.",
                    isGranted = hasNotif,
                    onClick = {
                        val intent = viewModel.notificationRepository.openNotificationSettingsIntent()
                        viewModel.startActivitySafely(intent)
                    }
                )

                PermissionStatusTile(
                    title = "USAGE STATS ACCESS",
                    description = "Required for daily screen time limits.",
                    isGranted = hasUsage,
                    onClick = {
                        val intent = viewModel.usageStatsHelper.openUsageSettingsIntent()
                        viewModel.startActivitySafely(intent)
                    }
                )

                PermissionStatusTile(
                    title = "DEVICE ADMIN (LOCK NOW)",
                    description = "Required to lock screen when timers expire.",
                    isGranted = hasAdmin,
                    onClick = {
                        val intent = viewModel.deviceLockManager.getDeviceAdminIntent()
                        viewModel.startActivitySafely(intent)
                    }
                )

                PermissionStatusTile(
                    title = "ACCESSIBILITY INTERCEPTION",
                    description = "Optional real-time limit overlay trigger.",
                    isGranted = hasAccess,
                    onClick = {
                        val intent = UnplugAccessibilityService.openAccessibilitySettingsIntent()
                        viewModel.startActivitySafely(intent)
                    }
                )

                // 3. User Preferences
                Text(
                    text = "EXPERIENCE PREFERENCES",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.2.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("REDUCED MOTION", style = Typography.bodyMedium.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                        Text("Austere instant transitions", style = Typography.labelSmall.copy(color = NeutralGray))
                    }
                    Switch(
                        checked = reducedMotion,
                        onCheckedChange = { viewModel.toggleReducedMotion() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricChartreuse,
                            checkedTrackColor = SurfaceDark
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HAPTIC CONFIRMATIONS", style = Typography.bodyMedium.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                        Text("Subtle mechanical vibrations", style = Typography.labelSmall.copy(color = NeutralGray))
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { viewModel.toggleHaptics() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricChartreuse,
                            checkedTrackColor = SurfaceDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Clock Synchronization Option
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CLOCK SYNCHRONIZATION", style = Typography.bodyMedium.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                            val offsetMinutes = (timeOffsetMs / (1000L * 60L)).toInt()
                            val offsetText = if (offsetMinutes == 0) "Synchronized with system" else if (offsetMinutes > 0) "+$offsetMinutes minutes offset" else "$offsetMinutes minutes offset"
                            Text(offsetText, style = Typography.labelSmall.copy(color = ElectricChartreuse))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianBlack)
                                .clickable { viewModel.setTimeOffset(0L) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("RESET", style = Typography.labelSmall.copy(color = AlertRed, fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // -30 min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianBlack)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                                .clickable { viewModel.adjustTimeOffsetMinutes(-30) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-30M", style = Typography.labelSmall.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                        }

                        // -1 min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianBlack)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                                .clickable { viewModel.adjustTimeOffsetMinutes(-1) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-1M", style = Typography.labelSmall.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                        }

                        // +1 min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianBlack)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                                .clickable { viewModel.adjustTimeOffsetMinutes(1) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+1M", style = Typography.labelSmall.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                        }

                        // +30 min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianBlack)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                                .clickable { viewModel.adjustTimeOffsetMinutes(30) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+30M", style = Typography.labelSmall.copy(color = ChalkWhite, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Return to System Launcher Shortcut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            viewModel.startActivitySafely(intent)
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RETURN TO SYSTEM LAUNCHER",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.0.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PermissionStatusTile(
    title: String,
    description: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = Typography.labelSmall.copy(color = NeutralGray)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isGranted) ElectricChartreuse.copy(alpha = 0.2f) else AlertRed.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isGranted) "GRANTED" else "CONFIGURE",
                style = Typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = if (isGranted) ElectricChartreuse else AlertRed,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
