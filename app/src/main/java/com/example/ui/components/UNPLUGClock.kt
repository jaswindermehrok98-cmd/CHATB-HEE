package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UNPLUGClock(
    modifier: Modifier = Modifier,
    currentTimeMs: Long = System.currentTimeMillis(),
    isFocusActive: Boolean = false,
    focusRemainingSeconds: Int = 0,
    isDeviceTimerActive: Boolean = false,
    deviceTimerRemainingSeconds: Int = 0,
    onClockClick: () -> Unit = {}
) {
    val date = remember(currentTimeMs) { Date(currentTimeMs) }

    val hourFormat = remember { SimpleDateFormat("HH", Locale.getDefault()) }
    val minuteFormat = remember { SimpleDateFormat("mm", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("dd MMMM", Locale.getDefault()) }

    val hours = hourFormat.format(date)
    val minutes = minuteFormat.format(date)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClockClick() }
            .padding(vertical = 8.dp)
            .testTag("unplug_clock_container"),
        horizontalAlignment = Alignment.Start
    ) {
        // Active Status Pill (Focus or Device Timer)
        if (isFocusActive || isDeviceTimerActive) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(ElectricChartreuse.copy(alpha = 0.15f))
                    .border(1.dp, ElectricChartreuse.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("active_status_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(ElectricChartreuse)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val statusText = if (isFocusActive) {
                    val mins = focusRemainingSeconds / 60
                    val secs = focusRemainingSeconds % 60
                    "FOCUS ACTIVE // %02d:%02d".format(mins, secs)
                } else {
                    val mins = deviceTimerRemainingSeconds / 60
                    val secs = deviceTimerRemainingSeconds % 60
                    "LOCK IN // %02d:%02d".format(mins, secs)
                }
                Text(
                    text = statusText,
                    style = Typography.labelSmall.copy(
                        color = ElectricChartreuse,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Pure Minimalist Typographical Time Display (Split Weight & Negative Space)
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.testTag("unplug_clock_time")
        ) {
            Text(
                text = hours,
                style = Typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-3.0).sp,
                    color = ChalkWhite
                )
            )
            Text(
                text = ":",
                style = Typography.displayLarge.copy(
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Thin,
                    color = NeutralGray
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = minutes,
                style = Typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2.0).sp,
                    color = ElectricChartreuse
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Typographical Date and Day Line with refined letter spacing
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = dayFormat.format(date).uppercase(Locale.getDefault()),
                style = Typography.labelLarge.copy(
                    color = ChalkWhite.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.0.sp,
                    fontSize = 12.sp
                ),
                modifier = Modifier.testTag("unplug_clock_day")
            )
            Text(
                text = "/",
                style = Typography.labelLarge.copy(
                    color = NeutralGray,
                    fontSize = 12.sp
                )
            )
            Text(
                text = dateFormat.format(date).uppercase(Locale.getDefault()),
                style = Typography.labelLarge.copy(
                    color = NeutralGray,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.8.sp,
                    fontSize = 12.sp
                ),
                modifier = Modifier.testTag("unplug_clock_date")
            )
        }
    }
}
