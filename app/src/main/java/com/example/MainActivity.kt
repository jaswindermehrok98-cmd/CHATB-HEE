package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.ArchiveScreen
import com.example.ui.screens.DeviceLockScreen
import com.example.ui.screens.DropsScreen
import com.example.ui.screens.FilesScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.LabScreen
import com.example.ui.screens.LimitsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.SystemSettingsScreen
import com.example.ui.screens.TheReadScreen
import com.example.ui.setup.SetupScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val reducedMotion by viewModel.reducedMotion.collectAsState()

                // Intercept back presses to always return to HOME in a custom launcher
                BackHandler(enabled = currentScreen != Screen.HOME && currentScreen != Screen.ONBOARDING && currentScreen != Screen.SETUP) {
                    viewModel.navigateTo(Screen.HOME)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBlack),
                    containerColor = ObsidianBlack
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .safeDrawingPadding()
                    ) {
                        if (reducedMotion) {
                            ScreenContent(screen = currentScreen, viewModel = viewModel)
                        } else {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "ScreenTransition"
                            ) { targetScreen ->
                                ScreenContent(screen = targetScreen, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadApps()
        viewModel.refreshUsage()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
        viewModel.navigateTo(Screen.HOME)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        if (action == "com.example.ACTION_INTERVENTION") {
            val pkg = intent.getStringExtra("EXTRA_PACKAGE_NAME") ?: ""
            val label = intent.getStringExtra("EXTRA_APP_LABEL") ?: pkg
            val used = intent.getIntExtra("EXTRA_USED_MINUTES", 0)
            val limit = intent.getIntExtra("EXTRA_LIMIT_MINUTES", 0)
            viewModel.limitsManager.triggerIntervention(pkg, label, used, limit)
        }
    }
}

@Composable
fun ScreenContent(
    screen: Screen,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    when (screen) {
        Screen.HOME -> HomeScreen(viewModel = viewModel, modifier = modifier)
        Screen.INBOX -> InboxScreen(viewModel = viewModel, modifier = modifier)
        Screen.APP_DRAWER -> AppDrawerScreen(viewModel = viewModel, modifier = modifier)
        Screen.LIMITS -> LimitsScreen(viewModel = viewModel, modifier = modifier)
        Screen.FOCUS -> FocusScreen(viewModel = viewModel, modifier = modifier)
        Screen.DEVICE_LOCK -> DeviceLockScreen(viewModel = viewModel, modifier = modifier)
        Screen.THE_READ -> TheReadScreen(viewModel = viewModel, modifier = modifier)
        Screen.DROPS -> DropsScreen(viewModel = viewModel, modifier = modifier)
        Screen.LAB -> LabScreen(viewModel = viewModel, modifier = modifier)
        Screen.FILES -> FilesScreen(viewModel = viewModel, modifier = modifier)
        Screen.ARCHIVE -> ArchiveScreen(viewModel = viewModel, modifier = modifier)
        Screen.SYSTEM -> SystemSettingsScreen(viewModel = viewModel, modifier = modifier)
        Screen.ONBOARDING -> OnboardingScreen(viewModel = viewModel, modifier = modifier)
        Screen.PREMIUM -> PremiumScreen(viewModel = viewModel, modifier = modifier)
        Screen.SETUP -> SetupScreen(viewModel = viewModel, modifier = modifier)
    }
}
