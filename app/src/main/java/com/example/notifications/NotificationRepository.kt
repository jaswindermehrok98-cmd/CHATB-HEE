package com.example.notifications

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepository(private val context: Context) {

    val notifications: Flow<List<UnplugNotificationItem>> = UnplugNotificationService.notifications

    val digest: Flow<NotificationDigest> = notifications.map { list ->
        val total = list.size
        val apps = list.map { it.packageName }.distinct().size
        val messages = list.count { it.category == TriageCategory.MESSAGE }
        val email = list.count { it.category == TriageCategory.UPDATE && (it.packageName.contains("mail") || it.packageName.contains("gmail")) }
        val social = list.count { it.packageName.contains("insta") || it.packageName.contains("twitter") || it.packageName.contains("face") || it.packageName.contains("reddit") }
        val reminders = list.count { it.category == TriageCategory.REMINDER }
        val other = total - messages - email - social - reminders

        NotificationDigest(
            totalCount = total,
            appCount = apps,
            messagesCount = messages,
            emailCount = email,
            socialCount = social,
            remindersCount = reminders,
            otherCount = other.coerceAtLeast(0)
        )
    }

    fun isNotificationAccessGranted(): Boolean {
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        return enabledPackages.contains(context.packageName)
    }

    fun openNotificationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun reply(key: String, text: String): Boolean {
        return UnplugNotificationService.replyToNotification(context, key, replyText = text)
    }

    fun openNotification(key: String): Boolean {
        return UnplugNotificationService.openNotification(key)
    }

    fun dismissNotification(key: String): Boolean {
        return UnplugNotificationService.dismissNotification(key)
    }

    fun clearAll(): Boolean {
        return UnplugNotificationService.clearAllDismissible()
    }

    fun getDefaultQuickReplies(): List<String> {
        return listOf(
            "Yes.",
            "Noted.",
            "On my way.",
            "I'll reply later.",
            "Thanks."
        )
    }
}
