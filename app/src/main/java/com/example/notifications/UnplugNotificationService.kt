package com.example.notifications

import android.app.Notification
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UnplugNotificationService : NotificationListenerService() {

    companion object {
        private const val TAG = "UnplugNotifService"

        private val _notifications = MutableStateFlow<List<UnplugNotificationItem>>(emptyList())
        val notifications: StateFlow<List<UnplugNotificationItem>> = _notifications.asStateFlow()

        @Volatile
        private var instance: UnplugNotificationService? = null

        fun isServiceConnected(): Boolean = instance != null

        fun replyToNotification(context: Context, key: String, replyText: String): Boolean {
            val service = instance ?: return false
            return service.performReply(key, replyText)
        }

        fun openNotification(key: String): Boolean {
            val service = instance ?: return false
            return service.performOpen(key)
        }

        fun dismissNotification(key: String): Boolean {
            val service = instance ?: return false
            return try {
                service.cancelNotification(key)
                service.refreshNotifications()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel notification", e)
                false
            }
        }

        fun clearAllDismissible(): Boolean {
            val service = instance ?: return false
            return try {
                service.cancelAllNotifications()
                service.refreshNotifications()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel all", e)
                false
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        refreshNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        refreshNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        refreshNotifications()
    }

    private fun refreshNotifications() {
        try {
            val active = activeNotifications ?: return
            val pm = packageManager
            val items = active
                .filter { it.packageName != packageName }
                .mapNotNull { sbn ->
                    val extras = sbn.notification?.extras ?: return@mapNotNull null
                    val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
                    val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
                        ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

                    if (title.isBlank() && text.isBlank()) return@mapNotNull null

                    val appName = try {
                        val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        sbn.packageName
                    }

                    val canReply = findReplyAction(sbn.notification) != null
                    val category = triageNotification(sbn.packageName, title, text, sbn.notification)

                    UnplugNotificationItem(
                        key = sbn.key,
                        packageName = sbn.packageName,
                        appName = appName,
                        title = title,
                        text = text,
                        postTime = sbn.postTime,
                        category = category,
                        canReply = canReply,
                        hasOpenAction = sbn.notification.contentIntent != null,
                        isOngoing = sbn.isOngoing
                    )
                }
                .sortedByDescending { it.postTime }

            _notifications.value = items
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing notifications", e)
        }
    }

    private fun triageNotification(
        pkg: String,
        title: String,
        text: String,
        notification: Notification
    ): TriageCategory {
        val lowerPkg = pkg.lowercase()
        val combined = "$title $text".lowercase()

        val isMsgApp = lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") ||
                lowerPkg.contains("signal") || lowerPkg.contains("message") || lowerPkg.contains("sms")

        val isEmailApp = lowerPkg.contains("mail") || lowerPkg.contains("gmail") || lowerPkg.contains("outlook")

        if (notification.category == Notification.CATEGORY_CALL ||
            notification.category == Notification.CATEGORY_ALARM ||
            combined.contains("urgent") || combined.contains("emergency") ||
            combined.contains("security alert") || combined.contains("otp")
        ) {
            return TriageCategory.IMPORTANT
        }

        if (isMsgApp || notification.category == Notification.CATEGORY_MESSAGE) {
            return TriageCategory.MESSAGE
        }

        if (notification.category == Notification.CATEGORY_REMINDER || notification.category == Notification.CATEGORY_EVENT) {
            return TriageCategory.REMINDER
        }

        if (isEmailApp || lowerPkg.contains("system") || lowerPkg.contains("download")) {
            return TriageCategory.UPDATE
        }

        return TriageCategory.LOW_PRIORITY
    }

    private fun findReplyAction(notification: Notification?): Pair<Notification.Action, RemoteInput>? {
        val actions = notification?.actions ?: return null
        for (action in actions) {
            val remoteInputs = action.remoteInputs ?: continue
            for (input in remoteInputs) {
                if (input.allowFreeFormInput) {
                    return Pair(action, input)
                }
            }
        }
        return null
    }

    private fun performReply(key: String, text: String): Boolean {
        try {
            val active = activeNotifications ?: return false
            val sbn = active.firstOrNull { it.key == key } ?: return false
            val replyPair = findReplyAction(sbn.notification) ?: return false

            val (action, remoteInput) = replyPair
            val intent = Intent()
            val bundle = Bundle().apply {
                putCharSequence(remoteInput.resultKey, text)
            }
            RemoteInput.addResultsToIntent(arrayOf(remoteInput), intent, bundle)
            action.actionIntent.send(this, 0, intent)
            refreshNotifications()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Reply failed", e)
            return false
        }
    }

    private fun performOpen(key: String): Boolean {
        try {
            val active = activeNotifications ?: return false
            val sbn = active.firstOrNull { it.key == key } ?: return false
            val contentIntent = sbn.notification?.contentIntent ?: return false
            contentIntent.send()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Open notification failed", e)
            return false
        }
    }
}
