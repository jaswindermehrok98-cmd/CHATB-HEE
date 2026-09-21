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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focus.SoundPreset
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
fun PremiumScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isPremiumUnlocked by viewModel.isPremiumUnlocked.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val activeAudioPreset by viewModel.activeAudioPreset.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val vaultHiddenPackages by viewModel.vaultHiddenPackages.collectAsState()
    val proTheme by viewModel.proTheme.collectAsState()

    var inputPasscode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("premium_screen")
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
                        contentDescription = "Back to Home",
                        tint = ChalkWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UNPLUG SOVEREIGN PRO",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isPremiumUnlocked) ElectricChartreuse else SurfaceDark)
                        .border(1.dp, if (isPremiumUnlocked) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isPremiumUnlocked) "PRO UNLOCKED" else "PRO LOCKED",
                        style = Typography.labelSmall.copy(
                            color = if (isPremiumUnlocked) ObsidianBlack else NeutralGray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isPremiumUnlocked) {
                // LOCKED PAYWALL / PASSCODE TERMINAL
                LockedPaywallView(
                    inputPasscode = inputPasscode,
                    onPasscodeChange = {
                        inputPasscode = it
                        errorMessage = null
                    },
                    errorMessage = errorMessage,
                    onUnlock = {
                        val success = viewModel.unlockPremiumWithCode(inputPasscode)
                        if (success) {
                            showSuccessBanner = true
                            errorMessage = null
                        } else {
                            errorMessage = "INCORRECT CODE. ENTER 2013 TO UNLOCK."
                        }
                    }
                )
            } else {
                // FULL UNLOCKED PREMIUM SUITE (Never asks for code again!)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        // Unlocked Badge Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceDark)
                                .border(1.dp, ElectricChartreuse, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ElectricChartreuse),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Lifetime Unlocked",
                                        tint = ObsidianBlack,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "SOVEREIGN PASS // ACTIVE",
                                        style = Typography.labelLarge.copy(
                                            color = ElectricChartreuse,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp
                                        )
                                    )
                                    Text(
                                        text = "Permanent Lifetime License Unlocked with Code 2013. Offline Sovereign Privileges Enabled.",
                                        style = Typography.bodySmall.copy(color = NeutralGray)
                                    )
                                }
                            }
                        }
                    }

                    // Feature 1: Acoustic Focus Synthesizer
                    item {
                        SynthesizerSection(
                            isPlaying = isAudioPlaying,
                            activePreset = activeAudioPreset,
                            onTogglePreset = { preset -> viewModel.toggleAudio(preset) },
                            onStop = { viewModel.stopAudio() }
                        )
                    }

                    // Feature 2: Pro Attention Telemetry & Autopsy
                    item {
                        AttentionAutopsySection()
                    }

                    // Feature 3: Stealth App Vault
                    item {
                        AppVaultSection(
                            allApps = allApps,
                            hiddenPackages = vaultHiddenPackages,
                            onToggleHide = { pkg -> viewModel.toggleVaultApp(pkg) }
                        )
                    }

                    // Feature 4: Theme Engine
                    item {
                        ProThemeSection(
                            currentTheme = proTheme,
                            onSelectTheme = { theme -> viewModel.setProTheme(theme) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedPaywallView(
    inputPasscode: String,
    onPasscodeChange: (String) -> Unit,
    errorMessage: String?,
    onUnlock: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Premium Icon",
                            tint = ElectricChartreuse,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "PREMIUM SOVEREIGN SUITE",
                            style = Typography.labelLarge.copy(
                                color = ElectricChartreuse,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The ultimate hardware-minimalist toolkit for absolute digital sovereignty and cognitive clarity.",
                        style = Typography.bodyMedium.copy(color = ChalkWhite)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pricing / Pass Information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LIFETIME ACCESS",
                                style = Typography.labelSmall.copy(color = NeutralGray)
                            )
                            Text(
                                text = "$49.00 USD",
                                style = Typography.headlineMedium.copy(
                                    color = ChalkWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceDarkHover)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "NO SUBSCRIPTIONS",
                                style = Typography.labelSmall.copy(color = ElectricChartreuse)
                            )
                        }
                    }
                }
            }
        }

        // Feature List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "INCLUDED PRO SUITE FEATURES:",
                    style = Typography.labelSmall.copy(color = NeutralGray, letterSpacing = 1.2.sp)
                )

                ProFeatureBullet(
                    icon = Icons.Default.GraphicEq,
                    title = "Binaural Acoustic Synthesizer",
                    desc = "Real-time 432Hz Solfeggio focus tones, Brown noise, and 10Hz Alpha beats played offline."
                )

                ProFeatureBullet(
                    icon = Icons.Default.Shield,
                    title = "Stealth App Vault & Cloaking",
                    desc = "Cloak downloaded apps from the drawer and protect sensitive workflows."
                )

                ProFeatureBullet(
                    icon = Icons.Default.Tune,
                    title = "Digital Autopsy & Distraction Telemetry",
                    desc = "Deep hourly telemetry, compulsive check probability, and impulse vulnerability graphs."
                )

                ProFeatureBullet(
                    icon = Icons.Default.VpnKey,
                    title = "OLED Pure Monochrome Visual Engine",
                    desc = "True black zero-bleed OLED rendering, Solar Amber & Matrix Green austerity skins."
                )
            }
        }

        // Code Entry Terminal
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, ElectricChartreuse, RoundedCornerShape(8.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Code Key",
                            tint = ElectricChartreuse,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "ENTER UNLOCK CODE",
                            style = Typography.labelLarge.copy(
                                color = ElectricChartreuse,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter your 4-digit Sovereign license code (2013) to unlock all features permanently on this device. Once entered, the app will never ask for it again.",
                        style = Typography.bodySmall.copy(color = NeutralGray)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputPasscode,
                        onValueChange = onPasscodeChange,
                        placeholder = {
                            Text(
                                "Enter code (2013)",
                                style = Typography.bodyMedium.copy(color = NeutralGray)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricChartreuse,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = ChalkWhite,
                            unfocusedTextColor = ChalkWhite,
                            focusedContainerColor = ObsidianBlack,
                            unfocusedContainerColor = ObsidianBlack
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onUnlock() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("premium_code_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            style = Typography.labelSmall.copy(color = AlertRed)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricChartreuse)
                            .clickable { onUnlock() }
                            .padding(vertical = 14.dp)
                            .testTag("activate_premium_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ACTIVATE WITH CODE // 2013",
                            style = Typography.labelLarge.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold,
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
private fun ProFeatureBullet(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ElectricChartreuse,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = Typography.bodyMedium.copy(
                    color = ChalkWhite,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = desc,
                style = Typography.bodySmall.copy(color = NeutralGray)
            )
        }
    }
}

@Composable
private fun SynthesizerSection(
    isPlaying: Boolean,
    activePreset: SoundPreset,
    onTogglePreset: (SoundPreset) -> Unit,
    onStop: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio Synthesizer",
                        tint = ElectricChartreuse,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "NEURAL ACOUSTIC SYNTHESIZER",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AlertRed)
                            .clickable { onStop() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "STOP AUDIO",
                            style = Typography.labelSmall.copy(color = ChalkWhite, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Generative pure mathematics audio synthesizer rendered in real-time. Zero streaming or network required.",
                style = Typography.bodySmall.copy(color = NeutralGray)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SoundPreset.values().forEach { preset ->
                    val isCurrentPlaying = isPlaying && activePreset == preset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrentPlaying) SurfaceDarkHover else ObsidianBlack)
                            .border(
                                1.dp,
                                if (isCurrentPlaying) ElectricChartreuse else SurfaceBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onTogglePreset(preset) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.title,
                                style = Typography.bodyMedium.copy(
                                    color = if (isCurrentPlaying) ElectricChartreuse else ChalkWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${preset.frequencyDesc} • ${preset.benefits}",
                                style = Typography.labelSmall.copy(color = NeutralGray, fontSize = 10.sp)
                            )
                        }

                        Icon(
                            imageVector = if (isCurrentPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isCurrentPlaying) "Stop" else "Play",
                            tint = if (isCurrentPlaying) ElectricChartreuse else ChalkWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttentionAutopsySection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Autopsy Icon",
                    tint = ElectricChartreuse,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "DIGITAL AUTOPSY & DOPAMINE HEATMAP",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.2.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AutopsyMetricCard(
                    title = "COMPULSIVE RISK",
                    value = "14%",
                    status = "OPTIMAL",
                    statusColor = ElectricChartreuse,
                    modifier = Modifier.weight(1f)
                )
                AutopsyMetricCard(
                    title = "PEAK VULNERABILITY",
                    value = "21:30",
                    status = "NIGHT WINDOW",
                    statusColor = NeutralGray,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AutopsyMetricCard(
                    title = "FOCUS STAMINA",
                    value = "94/100",
                    status = "+18% THIS WEEK",
                    statusColor = ElectricChartreuse,
                    modifier = Modifier.weight(1f)
                )
                AutopsyMetricCard(
                    title = "DOPAMINE VELOCITY",
                    value = "0.22/hr",
                    status = "SOVEREIGN",
                    statusColor = ElectricChartreuse,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AutopsyMetricCard(
    title: String,
    value: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianBlack)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = Typography.labelSmall.copy(color = NeutralGray, fontSize = 9.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = Typography.titleLarge.copy(color = ChalkWhite, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = status,
                style = Typography.labelSmall.copy(color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun AppVaultSection(
    allApps: List<com.example.apps.AppInfo>,
    hiddenPackages: Set<String>,
    onToggleHide: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "App Vault",
                        tint = ElectricChartreuse,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "STEALTH APP VAULT",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                Text(
                    text = "${hiddenPackages.size} CLOAKED",
                    style = Typography.labelSmall.copy(color = ElectricChartreuse)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Hide selected downloaded apps from appearing in the main drawer to prevent impulsive tapping.",
                style = Typography.bodySmall.copy(color = NeutralGray)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                allApps.take(6).forEach { app ->
                    val isHidden = hiddenPackages.contains(app.packageName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(ObsidianBlack)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.label,
                            style = Typography.bodyMedium.copy(color = ChalkWhite)
                        )

                        Switch(
                            checked = isHidden,
                            onCheckedChange = { onToggleHide(app.packageName) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBlack,
                                checkedTrackColor = ElectricChartreuse,
                                uncheckedThumbColor = NeutralGray,
                                uncheckedTrackColor = SurfaceDark
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProThemeSection(
    currentTheme: String,
    onSelectTheme: (String) -> Unit
) {
    val themes = listOf(
        "OLED_BLACK" to "OLED True Monochrome",
        "MATRIX_GREEN" to "Matrix Cyberpunk 1999",
        "SOLAR_AMBER" to "Solar Amber Terminal",
        "INDUSTRIAL_SLATE" to "Industrial Titanium Slate"
    )

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
                text = "AUSTERITY VISUAL THEME ENGINE",
                style = Typography.labelLarge.copy(
                    color = ChalkWhite,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                themes.forEach { (key, name) ->
                    val isSelected = currentTheme == key
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) SurfaceDarkHover else ObsidianBlack)
                            .border(
                                1.dp,
                                if (isSelected) ElectricChartreuse else SurfaceBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectTheme(key) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = Typography.bodyMedium.copy(
                                color = if (isSelected) ElectricChartreuse else ChalkWhite,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
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
