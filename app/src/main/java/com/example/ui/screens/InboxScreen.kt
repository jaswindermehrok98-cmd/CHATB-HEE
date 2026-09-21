package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notifications.TriageCategory
import com.example.notifications.UnplugNotificationItem
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InboxScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val digest by viewModel.notificationDigest.collectAsState()
    val hasNotifAccess = viewModel.notificationRepository.isNotificationAccessGranted()

    var selectedCategoryFilter by remember { mutableStateOf<TriageCategory?>(null) }

    val filteredNotifications = remember(notifications, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            notifications
        } else {
            notifications.filter { it.category == selectedCategoryFilter }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("inbox_screen")
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
                        text = "INBOX // TRIAGE",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                if (notifications.isNotEmpty()) {
                    Text(
                        text = "CLEAR ALL",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.0.sp
                        ),
                        modifier = Modifier
                            .clickable { viewModel.clearAllNotifications() }
                            .padding(4.dp)
                            .testTag("clear_all_notifications_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasNotifAccess) {
                // Permission Callout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, AlertRed, RoundedCornerShape(8.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "NOTIFICATION ACCESS REQUIRED",
                            style = Typography.titleMedium.copy(
                                color = AlertRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "UNPLUG LABS processes notifications strictly on-device to categorize triage and provide direct quick replies without transmitting your personal data.",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricChartreuse)
                                .clickable {
                                    val intent = viewModel.notificationRepository.openNotificationSettingsIntent()
                                    viewModel.startActivitySafely(intent)
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("grant_notification_access_btn")
                        ) {
                            Text(
                                text = "ENABLE NOTIFICATION ACCESS",
                                style = Typography.labelSmall.copy(
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.0.sp
                                )
                            )
                        }
                    }
                }
            } else {
                // Category Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        label = "ALL (${digest.totalCount})",
                        isSelected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null }
                    )
                    FilterChip(
                        label = "MSGS (${digest.messagesCount})",
                        isSelected = selectedCategoryFilter == TriageCategory.MESSAGE,
                        onClick = { selectedCategoryFilter = TriageCategory.MESSAGE }
                    )
                    FilterChip(
                        label = "IMPORTANT",
                        isSelected = selectedCategoryFilter == TriageCategory.IMPORTANT,
                        onClick = { selectedCategoryFilter = TriageCategory.IMPORTANT }
                    )
                    FilterChip(
                        label = "UPDATES",
                        isSelected = selectedCategoryFilter == TriageCategory.UPDATE,
                        onClick = { selectedCategoryFilter = TriageCategory.UPDATE }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "INBOX ZERO",
                                style = Typography.titleLarge.copy(
                                    color = NeutralGray,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.0.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No unread items requiring your attention.",
                                style = Typography.bodyMedium.copy(color = NeutralGray)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredNotifications, key = { it.key }) { item ->
                            NotificationTriageCard(
                                item = item,
                                defaultQuickReplies = viewModel.notificationRepository.getDefaultQuickReplies(),
                                onReply = { text -> viewModel.replyToNotification(item.key, text) },
                                onOpen = { viewModel.openNotification(item.key) },
                                onDismiss = { viewModel.dismissNotification(item.key) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) ElectricChartreuse else SurfaceDark)
            .border(1.dp, if (isSelected) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ObsidianBlack else ChalkWhite,
                letterSpacing = 0.8.sp
            )
        )
    }
}

@Composable
private fun NotificationTriageCard(
    item: UnplugNotificationItem,
    defaultQuickReplies: List<String>,
    onReply: (String) -> Unit,
    onOpen: () -> Unit,
    onDismiss: () -> Unit
) {
    var isReplying by remember { mutableStateOf(false) }
    var customReplyText by remember { mutableStateOf("") }
    var replySentConfirmation by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .testTag("notification_card_${item.key}")
    ) {
        // App Name and Category & Timestamp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.appName.uppercase(),
                    style = Typography.labelSmall.copy(
                        color = ElectricChartreuse,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(SurfaceDarkHover)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category.name,
                        style = Typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = NeutralGray
                        )
                    )
                }
            }

            Text(
                text = timeFormat.format(Date(item.postTime)),
                style = Typography.labelSmall.copy(color = NeutralGray)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (item.title.isNotBlank()) {
            Text(
                text = item.title,
                style = Typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        if (item.text.isNotBlank()) {
            Text(
                text = item.text,
                style = Typography.bodyMedium.copy(
                    color = if (item.title.isBlank()) ChalkWhite else NeutralGray
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.canReply) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricChartreuse)
                            .clickable { isReplying = !isReplying }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("reply_toggle_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isReplying) "CANCEL" else "REPLY",
                            style = Typography.labelSmall.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDarkHover)
                        .clickable { onOpen() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("open_app_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OPEN APP",
                        style = Typography.labelSmall.copy(
                            color = ChalkWhite,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            Text(
                text = "DISMISS",
                style = Typography.labelSmall.copy(
                    color = NeutralGray,
                    letterSpacing = 0.8.sp
                ),
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(4.dp)
                    .testTag("dismiss_notification_btn")
            )
        }

        // Expanded Direct RemoteInput Quick Reply View
        AnimatedVisibility(visible = isReplying) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "QUICK REPLIES",
                    style = Typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = NeutralGray,
                        letterSpacing = 1.0.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick reply chip suggestions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    defaultQuickReplies.take(3).forEach { replyText ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceDarkHover)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                                .clickable {
                                    onReply(replyText)
                                    replySentConfirmation = true
                                    isReplying = false
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = replyText,
                                style = Typography.labelSmall.copy(
                                    color = ChalkWhite,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom text entry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customReplyText,
                        onValueChange = { customReplyText = it },
                        placeholder = {
                            Text(
                                "Type deliberate reply...",
                                style = Typography.bodyMedium.copy(color = NeutralGray)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricChartreuse,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = ChalkWhite,
                            unfocusedTextColor = ChalkWhite
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (customReplyText.isNotBlank()) {
                                onReply(customReplyText)
                                customReplyText = ""
                                replySentConfirmation = true
                                isReplying = false
                            }
                        }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_reply_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricChartreuse)
                            .clickable {
                                if (customReplyText.isNotBlank()) {
                                    onReply(customReplyText)
                                    customReplyText = ""
                                    replySentConfirmation = true
                                    isReplying = false
                                }
                            }
                            .testTag("send_reply_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Reply",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (replySentConfirmation) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = ElectricChartreuse,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "REPLY DISPATCHED DIRECTLY",
                    style = Typography.labelSmall.copy(
                        color = ElectricChartreuse,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
