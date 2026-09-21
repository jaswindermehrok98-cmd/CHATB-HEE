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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notifications.NotificationDigest
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun InboxTriageSummary(
    digest: NotificationDigest,
    hasNotificationAccess: Boolean,
    onOpenInbox: () -> Unit,
    onEnableAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable {
                if (hasNotificationAccess) onOpenInbox() else onEnableAccess()
            }
            .padding(16.dp)
            .testTag("inbox_triage_summary")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "INBOX",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
                if (digest.totalCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricChartreuse.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${digest.totalCount} ACTIVE",
                            style = Typography.labelSmall.copy(
                                color = ElectricChartreuse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open Inbox",
                tint = NeutralGray,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!hasNotificationAccess) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AlertRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ENABLE NOTIFICATION ACCESS TO TRIAGE",
                    style = Typography.labelSmall.copy(
                        color = NeutralGray,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        } else if (digest.totalCount == 0) {
            Text(
                text = "No pending notifications. Attention clear.",
                style = Typography.bodyMedium.copy(color = NeutralGray)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TriageStatItem(label = "MESSAGES", count = digest.messagesCount, isHighlight = digest.messagesCount > 0)
                TriageStatItem(label = "EMAIL", count = digest.emailCount)
                TriageStatItem(label = "SOCIAL", count = digest.socialCount)
                TriageStatItem(label = "REMINDERS", count = digest.remindersCount)
            }
        }
    }
}

@Composable
private fun TriageStatItem(
    label: String,
    count: Int,
    isHighlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(
                color = NeutralGray,
                letterSpacing = 0.8.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$count",
            style = Typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) ElectricChartreuse else ChalkWhite
            )
        )
    }
}
