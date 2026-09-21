package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limits.UnplugAccessibilityService
import com.example.ui.MainViewModel
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

data class OnboardingStep(
    val stepIndex: String,
    val headline: String,
    val subheadline: String,
    val description: String,
    val actionType: ActionType = ActionType.NONE,
    val icon: ImageVector = Icons.Default.Security
)

enum class ActionType {
    NONE,
    HOME_ROLE,
    NOTIFICATION,
    USAGE,
    ACCESSIBILITY,
    DEVICE_ADMIN
}

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isHome = viewModel.isHomeApp()
    val hasNotif = viewModel.notificationRepository.isNotificationAccessGranted()
    val hasUsage = viewModel.usageStatsHelper.hasUsagePermission()
    val hasAccess = UnplugAccessibilityService.isRunning()
    val hasAdmin = viewModel.deviceLockManager.isDeviceAdminActive()

    val steps = remember(isHome, hasNotif, hasUsage, hasAccess, hasAdmin) {
        listOf(
            OnboardingStep(
                stepIndex = "01",
                headline = "AN INSTRUMENT.\nNOT A SLOT MACHINE.",
                subheadline = "THE UNPLUG MANIFESTO",
                description = "Modern home screens are engineered by ad platforms to trigger dopamine loops. OFFLINE OS strips away the casino to give you quiet sovereignty.",
                icon = Icons.Default.VpnKey
            ),
            OnboardingStep(
                stepIndex = "02",
                headline = "MAKE UNPLUG\nYOUR HOME.",
                subheadline = "DEFAULT LAUNCHER ROLE",
                description = "Set UNPLUG as your primary Android launcher to replace the distracting default home screen.",
                actionType = ActionType.HOME_ROLE,
                icon = Icons.Default.Speed
            ),
            OnboardingStep(
                stepIndex = "03",
                headline = "CONNECT\nYOUR INBOX.",
                subheadline = "NOTIFICATION TRIAGE",
                description = "Organize incoming notifications and reply directly without opening rabbit hole applications.",
                actionType = ActionType.NOTIFICATION,
                icon = Icons.Default.NotificationsActive
            ),
            OnboardingStep(
                stepIndex = "04",
                headline = "MEASURE\nAPP USAGE.",
                subheadline = "CONSCIOUS LIMITS",
                description = "Enable Android usage stats to track daily time budgets and enforce calm interventions.",
                actionType = ActionType.USAGE,
                icon = Icons.Default.Timer
            ),
            OnboardingStep(
                stepIndex = "05",
                headline = "ARM PHYSICAL\nSCREEN LOCK.",
                subheadline = "HARDWARE DEVICE ADMIN",
                description = "Allow UNPLUG to securely lock the device screen when your focus or device timer ends.",
                actionType = ActionType.DEVICE_ADMIN,
                icon = Icons.Default.Security
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }

    // Pulsing glow animation for cinematic video feel
    val pulseAnim = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(24.dp)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ElectricChartreuse.copy(alpha = pulseAnim.value))
                    )
                    Text(
                        text = "UNPLUG LABS® // CINEMATIC SETUP",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                Text(
                    text = "SKIP SETUP",
                    style = Typography.labelSmall.copy(
                        color = NeutralGray,
                        letterSpacing = 1.0.sp
                    ),
                    modifier = Modifier
                        .clickable { viewModel.completeOnboarding() }
                        .padding(4.dp)
                )
            }

            // Animated Step Content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + slideInHorizontally { it / 3 })
                        .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutHorizontally { -it / 3 })
                },
                label = "onboarding_step_animation",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) { stepIdx ->
                val step = steps[stepIdx]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricChartreuse.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = ElectricChartreuse,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${step.stepIndex} // ${step.subheadline}",
                            style = Typography.labelMedium.copy(
                                color = ElectricChartreuse,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = step.headline,
                        style = Typography.displaySmall.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = ChalkWhite,
                            lineHeight = 38.sp,
                            letterSpacing = (-1.0).sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = step.description,
                                style = Typography.bodyLarge.copy(
                                    color = ChalkWhite,
                                    lineHeight = 24.sp
                                )
                            )

                            // Interactive capability trigger if actionType is set
                            if (step.actionType != ActionType.NONE) {
                                Spacer(modifier = Modifier.height(16.dp))
                                val isConnected = when (step.actionType) {
                                    ActionType.HOME_ROLE -> isHome
                                    ActionType.NOTIFICATION -> hasNotif
                                    ActionType.USAGE -> hasUsage
                                    ActionType.ACCESSIBILITY -> hasAccess
                                    ActionType.DEVICE_ADMIN -> hasAdmin
                                    else -> false
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isConnected) ElectricChartreuse.copy(alpha = 0.2f) else SurfaceDark)
                                        .border(1.dp, if (isConnected) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(6.dp))
                                        .clickable {
                                            when (step.actionType) {
                                                ActionType.HOME_ROLE -> {
                                                    val intent = viewModel.getHomeRoleIntent()
                                                    viewModel.startActivitySafely(intent)
                                                }
                                                ActionType.NOTIFICATION -> {
                                                    val intent = viewModel.notificationRepository.openNotificationSettingsIntent()
                                                    viewModel.startActivitySafely(intent)
                                                }
                                                ActionType.USAGE -> {
                                                    val intent = viewModel.usageStatsHelper.openUsageSettingsIntent()
                                                    viewModel.startActivitySafely(intent)
                                                }
                                                ActionType.DEVICE_ADMIN -> {
                                                    val intent = viewModel.deviceLockManager.getDeviceAdminIntent()
                                                    viewModel.startActivitySafely(intent)
                                                }
                                                else -> {}
                                            }
                                        }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isConnected) "STATUS: CONNECTED" else "GRANT SYSTEM ACCESS",
                                        style = Typography.labelMedium.copy(
                                            color = if (isConnected) ElectricChartreuse else ChalkWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    if (isConnected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Connected",
                                            tint = ElectricChartreuse,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Footer Navigation
            Column {
                // Step Indicator Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { idx, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (idx == currentStep) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (idx == currentStep) ElectricChartreuse else SurfaceBorder)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Next or Enter OS Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElectricChartreuse)
                        .clickable {
                            if (currentStep < steps.size - 1) {
                                currentStep++
                            } else {
                                viewModel.completeOnboarding()
                            }
                        }
                        .padding(vertical = 16.dp)
                        .testTag("onboarding_continue_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentStep == steps.size - 1) "ENTER OFFLINE OS" else "NEXT STEP",
                            style = Typography.labelLarge.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ObsidianBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

