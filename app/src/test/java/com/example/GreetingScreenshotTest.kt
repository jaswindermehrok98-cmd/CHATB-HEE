package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.message.DailyMessage
import com.example.ui.components.TodayMessageCard
import com.example.ui.components.UNPLUGClock
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun unplug_home_components_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(
                    modifier = Modifier
                        .background(ObsidianBlack)
                        .padding(20.dp)
                ) {
                    TodayMessageCard(
                        message = DailyMessage(
                            id = "t1",
                            text = "Do what needs doing. Then put the phone down.",
                            category = "MORNING",
                            contextNote = "First light clarity"
                        ),
                        onCycleMessage = {}
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
