package com.example.notifications

enum class TriageCategory {
    IMPORTANT,
    MESSAGE,
    UPDATE,
    REMINDER,
    LOW_PRIORITY
}

data class UnplugNotificationItem(
    val key: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val postTime: Long,
    val category: TriageCategory,
    val canReply: Boolean = false,
    val hasOpenAction: Boolean = false,
    val isOngoing: Boolean = false
)

data class NotificationDigest(
    val totalCount: Int,
    val appCount: Int,
    val messagesCount: Int,
    val emailCount: Int,
    val socialCount: Int,
    val remindersCount: Int,
    val otherCount: Int
)
