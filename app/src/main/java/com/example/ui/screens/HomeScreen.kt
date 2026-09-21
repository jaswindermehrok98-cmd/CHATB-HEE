package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
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
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.EssentialAppsRow
import com.example.ui.components.InboxTriageSummary
import com.example.ui.components.InterventionDialog
import com.example.ui.components.QuickActionsBar
import com.example.ui.components.TodayMessageCard
import com.example.ui.components.UNPLUGClock
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkHover
import com.example.ui.theme.Typography
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material.icons.filled.Lock

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allApps by viewModel.allApps.collectAsState()
    val todayMessage by viewModel.todayMessage.collectAsState()
    val digest by viewModel.notificationDigest.collectAsState()
    val focusState by viewModel.focusState.collectAsState()
    val deviceTimerState by viewModel.deviceTimerState.collectAsState()
    val interventionState by viewModel.interventionState.collectAsState()
    val isPremiumUnlocked by viewModel.isPremiumUnlocked.collectAsState()
    val hasNotifAccess = viewModel.notificationRepository.isNotificationAccessGranted()
    val systemTime by viewModel.systemTime.collectAsState()

    val essentialApps = allApps.filter { app ->
        val name = app.label.lowercase()
        val pkg = app.packageName.lowercase()
        name == "phone" || name == "messages" || name == "camera" || name == "maps" ||
                pkg.contains("dialer") || pkg.contains("mms") || pkg.contains("maps")
    }.ifEmpty { allApps.take(4) }

    val downloadedApps = allApps.filter { it.isDownloaded }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("home_screen_container")
    ) {
        if (focusState.isActive) {
            // FOCUS HOME STATE (Minimalist single-task mode)
            FocusHomeView(
                focusRemainingSeconds = focusState.remainingSeconds,
                onOpenEssentials = { viewModel.navigateTo(Screen.APP_DRAWER) },
                onEndFocus = { viewModel.focusSessionManager.endFocus() }
            )
        } else {
            // STANDARD INTENTIONAL HOME SCREEN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Editorial Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UNPLUG LABS® // OFFLINE OS",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.5.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ElectricChartreuse)
                        )
                        Text(
                            text = if (isPremiumUnlocked) "SOVEREIGN PRO" else "LOCAL",
                            style = Typography.labelSmall.copy(
                                color = ElectricChartreuse,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                        )
                    }
                }

                // 1. Time / Clock
                UNPLUGClock(
                    currentTimeMs = systemTime,
                    isFocusActive = focusState.isActive,
                    focusRemainingSeconds = focusState.remainingSeconds,
                    isDeviceTimerActive = deviceTimerState.isActive,
                    deviceTimerRemainingSeconds = deviceTimerState.remainingSeconds,
                    onClockClick = { viewModel.navigateTo(Screen.FOCUS) }
                )

                // 2. Sovereign Pro Pass Banner (Requires code 2013 if locked)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, if (isPremiumUnlocked) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.navigateTo(Screen.PREMIUM) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("home_pro_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Pro Features",
                                tint = if (isPremiumUnlocked) ElectricChartreuse else ChalkWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (isPremiumUnlocked) "UNPLUG PRO SUITE // ACTIVE" else "UNPLUG PRO // SOVEREIGN SUITE",
                                    style = Typography.labelMedium.copy(
                                        color = if (isPremiumUnlocked) ElectricChartreuse else ChalkWhite,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.0.sp
                                    )
                                )
                                Text(
                                    text = if (isPremiumUnlocked) "432Hz Audio Synth • App Vault • Telemetry" else "Enter Code 2013 for Lifetime Access →",
                                    style = Typography.labelSmall.copy(color = NeutralGray, fontSize = 10.sp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isPremiumUnlocked) SurfaceDarkHover else ElectricChartreuse)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isPremiumUnlocked) "OPEN" else "CODE: 2013",
                                style = Typography.labelSmall.copy(
                                    color = if (isPremiumUnlocked) ElectricChartreuse else ObsidianBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // 3. Today's Message
                TodayMessageCard(
                    message = todayMessage,
                    onCycleMessage = { viewModel.cycleTodayMessage() }
                )

                // 4. Downloaded & Installed Apps Shelf
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DOWNLOADED & INSTALLED APPS",
                            style = Typography.labelSmall.copy(
                                color = NeutralGray,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "VIEW ALL (${allApps.size}) →",
                            style = Typography.labelSmall.copy(
                                color = ElectricChartreuse,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(Screen.APP_DRAWER) }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (downloadedApps.isNotEmpty()) {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(downloadedApps, key = { it.packageName }) { app ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceDark)
                                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                                        .clickable { viewModel.launchApp(app.packageName, app.label) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("downloaded_app_chip_${app.packageName}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (app.iconBitmap != null) {
                                            androidx.compose.foundation.Image(
                                                bitmap = app.iconBitmap.asImageBitmap(),
                                                contentDescription = app.label,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                            )
                                        }
                                        Text(
                                            text = app.label,
                                            style = Typography.bodyMedium.copy(
                                                color = ChalkWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceDark)
                                .clickable { viewModel.navigateTo(Screen.APP_DRAWER) }
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BROWSE ALL DOWNLOADED APPS (${allApps.size}) →",
                                style = Typography.labelMedium.copy(color = ElectricChartreuse)
                            )
                        }
                    }
                }

                // 5. Essential Apps
                EssentialAppsRow(
                    essentialApps = essentialApps,
                    onAppClick = { pkg, label -> viewModel.launchApp(pkg, label) },
                    onOpenDrawer = { viewModel.navigateTo(Screen.APP_DRAWER) }
                )

                // 6. Inbox Triage Summary
                InboxTriageSummary(
                    digest = digest,
                    hasNotificationAccess = hasNotifAccess,
                    onOpenInbox = { viewModel.navigateTo(Screen.INBOX) },
                    onEnableAccess = { viewModel.navigateTo(Screen.SYSTEM) }
                )

                // 7. System Quick Actions
                QuickActionsBar(
                    onSearchClick = { viewModel.navigateTo(Screen.APP_DRAWER) },
                    onInboxClick = { viewModel.navigateTo(Screen.INBOX) },
                    onFocusClick = { viewModel.navigateTo(Screen.FOCUS) },
                    onLimitsClick = { viewModel.navigateTo(Screen.LIMITS) },
                    onLockClick = { viewModel.navigateTo(Screen.DEVICE_LOCK) },
                    onLabClick = { viewModel.navigateTo(Screen.LAB) },
                    onSystemClick = { viewModel.navigateTo(Screen.SYSTEM) },
                    onProClick = { viewModel.navigateTo(Screen.PREMIUM) }
                )

                // UNPLUG Core Experiences (Layer B: The Read, Drops, Lab, Archive, Manuscript)
                UnplugExperiencesShelf(
                    onTheReadClick = { viewModel.startTheRead() },
                    onDropsClick = { viewModel.navigateTo(Screen.DROPS) },
                    onLabClick = { viewModel.navigateTo(Screen.LAB) },
                    onArchiveClick = { viewModel.navigateTo(Screen.ARCHIVE) },
                    onFilesClick = { viewModel.navigateTo(Screen.FILES) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Limit Intervention Overlay
        AnimatedVisibility(
            visible = interventionState.isInterventionActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            InterventionDialog(
                state = interventionState,
                onReturnHome = { viewModel.dismissIntervention() },
                onAddFiveMinutes = { viewModel.addInterventionOverride(interventionState.packageName, 5) },
                onChangeLimit = {
                    viewModel.dismissIntervention()
                    viewModel.navigateTo(Screen.LIMITS)
                }
            )
        }
    }
}

@Composable
private fun FocusHomeView(
    focusRemainingSeconds: Int,
    onOpenEssentials: () -> Unit,
    onEndFocus: () -> Unit
) {
    val mins = focusRemainingSeconds / 60
    val secs = focusRemainingSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FOCUS ACTIVE",
            style = Typography.labelLarge.copy(
                color = ElectricChartreuse,
                letterSpacing = 2.5.sp
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "%02d:%02d".format(mins, secs),
            style = Typography.displayLarge.copy(
                fontSize = 68.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2.0).sp,
                color = ChalkWhite
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "\"ONE THING AT A TIME.\"",
            style = Typography.titleMedium.copy(
                color = NeutralGray,
                letterSpacing = 1.2.sp
            )
        )

        Spacer(modifier = Modifier.height(44.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceDark)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { onOpenEssentials() }
                .padding(horizontal = 24.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OPEN ESSENTIAL TOOLS",
                style = Typography.labelLarge.copy(
                    color = ChalkWhite,
                    letterSpacing = 1.2.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .clickable { onEndFocus() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "End Focus",
                tint = NeutralGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "END FOCUS EARLY",
                style = Typography.labelSmall.copy(
                    color = NeutralGray,
                    letterSpacing = 1.0.sp
                )
            )
        }
    }
}

@Composable
private fun UnplugExperiencesShelf(
    onTheReadClick: () -> Unit,
    onDropsClick: () -> Unit,
    onLabClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onFilesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("experiences_shelf")
    ) {
        Text(
            text = "UNPLUG LABS // CORE EXPERIENCES",
            style = Typography.labelSmall.copy(
                color = NeutralGray,
                letterSpacing = 1.2.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExperienceCard(
                title = "THE READ",
                subtitle = "Perception",
                onClick = onTheReadClick,
                modifier = Modifier.weight(1f),
                testTag = "the_read_teaser_btn"
            )
            ExperienceCard(
                title = "DROPS",
                subtitle = "Artifacts",
                onClick = onDropsClick,
                modifier = Modifier.weight(1f),
                testTag = "drops_teaser_btn"
            )
            ExperienceCard(
                title = "LAB",
                subtitle = "Cognition",
                onClick = onLabClick,
                modifier = Modifier.weight(1f),
                testTag = "lab_teaser_btn"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExperienceCard(
                title = "ARCHIVE",
                subtitle = "History",
                onClick = onArchiveClick,
                modifier = Modifier.weight(1f),
                testTag = "archive_teaser_btn"
            )
            ExperienceCard(
                title = "MANUSCRIPT",
                subtitle = "Readings",
                onClick = onFilesClick,
                modifier = Modifier.weight(1f),
                testTag = "manuscript_teaser_btn"
            )
        }
    }
}

@Composable
private fun ExperienceCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Column {
            Text(
                text = title,
                style = Typography.labelSmall.copy(
                    color = ChalkWhite,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.0.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = Typography.labelSmall.copy(
                    color = NeutralGray,
                    fontSize = 9.sp
                )
            )
        }
    }
}
