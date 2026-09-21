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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.AppLimitEntity
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
import com.example.ui.theme.WarningAmber

@Composable
fun LimitsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val limits by viewModel.appLimits.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val usageInsight by viewModel.usageInsight.collectAsState()
    val hasUsageAccess = viewModel.usageStatsHelper.hasUsagePermission()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("limits_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.HOME) }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return Home",
                        tint = ChalkWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APP LIMITS & SESSIONS",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                Text(
                    text = "ADD +",
                    style = Typography.labelSmall.copy(
                        color = ElectricChartreuse,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    ),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.APP_DRAWER) }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Usage Permission Warning if not granted
            if (!hasUsageAccess) {
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
                            text = "USAGE ACCESS REQUIRED",
                            style = Typography.titleMedium.copy(
                                color = AlertRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Android requires Usage Access authorization to measure screen time and enforce intentional daily limits.",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricChartreuse)
                                .clickable {
                                    val intent = viewModel.usageStatsHelper.openUsageSettingsIntent()
                                    viewModel.startActivitySafely(intent)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("grant_usage_access_btn")
                        ) {
                            Text(
                                text = "ENABLE USAGE ACCESS",
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

            // Screen Time Overview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TODAY'S SCREEN TIME",
                            style = Typography.labelSmall.copy(
                                color = NeutralGray,
                                letterSpacing = 1.0.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val hrs = usageInsight.totalScreenTimeMinutes / 60
                        val mins = usageInsight.totalScreenTimeMinutes % 60
                        Text(
                            text = "${hrs}h ${mins}m",
                            style = Typography.headlineLarge.copy(
                                color = ChalkWhite,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "APP OPENS",
                            style = Typography.labelSmall.copy(
                                color = NeutralGray,
                                letterSpacing = 1.0.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${usageInsight.totalAppOpens}",
                            style = Typography.headlineLarge.copy(
                                color = ElectricChartreuse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CONFIGURED LIMITS",
                style = Typography.labelLarge.copy(
                    color = ChalkWhite,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (limits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO CONFIGURED LIMITS",
                            style = Typography.titleMedium.copy(
                                color = NeutralGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'ADD +' or open All Apps to assign daily limits.",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(limits, key = { it.packageName }) { limit ->
                        val app = allApps.firstOrNull { it.packageName == limit.packageName }
                        val label = app?.label ?: limit.packageName
                        val used = usageInsight.perAppMinutes[limit.packageName] ?: 0

                        LimitItemCard(
                            limit = limit,
                            label = label,
                            usedMinutes = used,
                            onStartSession = { mins -> viewModel.startSessionLimit(limit.packageName, mins) },
                            onRemove = { viewModel.removeAppLimit(limit.packageName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LimitItemCard(
    limit: AppLimitEntity,
    label: String,
    usedMinutes: Int,
    onStartSession: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val progress = (usedMinutes.toFloat() / limit.dailyLimitMinutes.toFloat()).coerceIn(0f, 1f)
    val isExceeded = usedMinutes >= limit.dailyLimitMinutes

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(
                1.dp,
                if (isExceeded) AlertRed else SurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .testTag("limit_card_${limit.packageName}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = Typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite
                )
            )

            Text(
                text = "$usedMinutes / ${limit.dailyLimitMinutes} MIN",
                style = Typography.labelLarge.copy(
                    color = if (isExceeded) AlertRed else ElectricChartreuse,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isExceeded) AlertRed else ElectricChartreuse,
            trackColor = SurfaceDarkHover,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Temporary session start buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDarkHover)
                        .clickable { onStartSession(10) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "10M SESSION",
                        style = Typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = ChalkWhite
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDarkHover)
                        .clickable { onStartSession(20) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "20M SESSION",
                        style = Typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = ChalkWhite
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Limit",
                tint = NeutralGray,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onRemove() }
            )
        }
    }
}
