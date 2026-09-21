package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun QuickActionsBar(
    onSearchClick: () -> Unit,
    onInboxClick: () -> Unit,
    onFocusClick: () -> Unit,
    onLimitsClick: () -> Unit,
    onLockClick: () -> Unit,
    onLabClick: () -> Unit,
    onSystemClick: () -> Unit,
    onProClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_actions_bar")
    ) {
        Text(
            text = "SYSTEM MODULES",
            style = Typography.labelSmall.copy(
                color = NeutralGray,
                letterSpacing = 1.2.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                label = "APPS",
                icon = Icons.Default.Search,
                onClick = onSearchClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "PRO",
                icon = Icons.Default.Star,
                onClick = onProClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "INBOX",
                icon = Icons.Default.Inbox,
                onClick = onInboxClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "FOCUS",
                icon = Icons.Default.HourglassTop,
                onClick = onFocusClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "LIMITS",
                icon = Icons.Default.Speed,
                onClick = onLimitsClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "LOCK",
                icon = Icons.Default.Lock,
                onClick = onLockClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "SYS",
                icon = Icons.Default.Settings,
                onClick = onSystemClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ChalkWhite,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = Typography.labelSmall.copy(
                fontSize = 9.sp,
                color = NeutralGray,
                letterSpacing = 0.5.sp
            )
        )
    }
}
