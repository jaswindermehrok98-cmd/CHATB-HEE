package com.example.ui.setup

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

data class SetupStepItem(
    val id: Int,
    val title: String,
    val description: String,
    val actionText: String,
    val icon: ImageVector,
    val checkStatus: (MainViewModel) -> Boolean,
    val onTrigger: (MainViewModel) -> Unit
)

@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isHome = viewModel.isHomeApp()
    val hasNotif = viewModel.notificationRepository.isNotificationAccessGranted()
    val hasUsage = viewModel.usageStatsHelper.hasUsagePermission()

    val steps = remember {
        listOf(
            SetupStepItem(
                id = 0,
                title = "HOME ROLE",
                description = "Make UNPLUG your default primary launcher so every home-press brings you straight into focus.",
                actionText = "CONFIGURE HOME ROLE",
                icon = Icons.Default.Speed,
                checkStatus = { vm -> vm.isHomeApp() },
                onTrigger = { vm ->
                    val intent = vm.getHomeRoleIntent()
                    vm.startActivitySafely(intent)
                }
            ),
            SetupStepItem(
                id = 1,
                title = "NOTIFICATIONS ACCESS",
                description = "Organize incoming app alerts in your local Inbox without opening feed algorithms or notification shade rabbit holes.",
                actionText = "CONNECT NOTIFICATION INBOX",
                icon = Icons.Default.NotificationsActive,
                checkStatus = { vm -> vm.notificationRepository.isNotificationAccessGranted() },
                onTrigger = { vm ->
                    val intent = vm.notificationRepository.openNotificationSettingsIntent()
                    vm.startActivitySafely(intent)
                }
            ),
            SetupStepItem(
                id = 2,
                title = "APP USAGE PERMISSION",
                description = "Grant local system usage stats access to calculate continuous application focus time limits.",
                actionText = "ENABLE USAGE DETAILS",
                icon = Icons.Default.Timer,
                checkStatus = { vm -> vm.usageStatsHelper.hasUsagePermission() },
                onTrigger = { vm ->
                    val intent = vm.usageStatsHelper.openUsageSettingsIntent()
                    vm.startActivitySafely(intent)
                }
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }
    val step = steps[currentStep]
    val isCompleted = step.checkStatus(viewModel)

    // Calculate dynamic setup progress
    val completedCount = steps.count { it.checkStatus(viewModel) }
    val progressFraction = completedCount.toFloat() / steps.size.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(500),
        label = "setup_progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(24.dp)
            .testTag("setup_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UNPLUG SETUP // CHECKLIST",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.5.sp
                        )
                    )

                    Text(
                        text = "SKIP",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.0.sp
                        ),
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(Screen.HOME) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape),
                    color = ElectricChartreuse,
                    trackColor = SurfaceBorder
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$completedCount OF ${steps.size} CAPABILITIES ACTIVE",
                    style = Typography.labelSmall.copy(
                        color = ElectricChartreuse,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    )
                )
            }

            // Checklist Transitions via AnimatedContent
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + slideInHorizontally { it / 3 })
                        .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutHorizontally { -it / 3 })
                },
                label = "checklist_step_animation",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) { targetIdx ->
                val currentItem = steps[targetIdx]
                val currentActive = currentItem.checkStatus(viewModel)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (currentActive) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (currentActive) "Active" else "Inactive",
                            tint = if (currentActive) ElectricChartreuse else NeutralGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "STEP 0${currentItem.id + 1} // ${currentItem.title}",
                            style = Typography.labelMedium.copy(
                                color = if (currentActive) ElectricChartreuse else ChalkWhite,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "GRANT ${currentItem.title} SYSTEM PERMISSION",
                        style = Typography.displaySmall.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = ChalkWhite,
                            lineHeight = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            Text(
                                text = currentItem.description,
                                style = Typography.bodyMedium.copy(
                                    color = ChalkWhite,
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (currentActive) ElectricChartreuse.copy(alpha = 0.15f) else ElectricChartreuse)
                                    .border(1.dp, ElectricChartreuse, RoundedCornerShape(6.dp))
                                    .clickable { currentItem.onTrigger(viewModel) }
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (currentActive) "STATUS: CONNECTED" else currentItem.actionText,
                                    style = Typography.labelLarge.copy(
                                        color = if (currentActive) ElectricChartreuse else ObsidianBlack,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.0.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Checklist Bottom Nav Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Step Trigger
                Text(
                    text = if (currentStep > 0) "PREVIOUS" else "",
                    style = Typography.labelSmall.copy(
                        color = NeutralGray,
                        letterSpacing = 1.0.sp
                    ),
                    modifier = Modifier
                        .clickable { if (currentStep > 0) currentStep-- }
                        .padding(8.dp)
                )

                // Next Step or Finish Trigger
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isCompleted || currentStep < steps.size - 1) ElectricChartreuse else SurfaceDark)
                        .clickable {
                            if (currentStep < steps.size - 1) {
                                currentStep++
                            } else {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (currentStep == steps.size - 1) "FINISH SETUP" else "NEXT STEP",
                            style = Typography.labelSmall.copy(
                                color = if (isCompleted || currentStep < steps.size - 1) ObsidianBlack else ChalkWhite,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (isCompleted || currentStep < steps.size - 1) ObsidianBlack else ChalkWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
