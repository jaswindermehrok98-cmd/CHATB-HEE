package com.example.limits

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import java.util.Calendar

data class UsageInsight(
    val totalScreenTimeMinutes: Int,
    val totalAppOpens: Int,
    val perAppMinutes: Map<String, Int>
)

class UsageStatsHelper(private val context: Context) {

    fun hasUsagePermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageSettingsIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun getTodayUsage(): UsageInsight {
        if (!hasUsagePermission()) {
            return UsageInsight(totalScreenTimeMinutes = 0, totalAppOpens = 0, perAppMinutes = emptyMap())
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return UsageInsight(0, 0, emptyMap())

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val statsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        var totalTimeMillis = 0L
        val appMinutes = mutableMapOf<String, Int>()

        for (stats in statsList) {
            val totalTime = stats.totalTimeInForeground
            if (totalTime > 0) {
                totalTimeMillis += totalTime
                val mins = (totalTime / 60000L).toInt()
                if (mins > 0) {
                    appMinutes[stats.packageName] = mins
                }
            }
        }

        val totalScreenMins = (totalTimeMillis / 60000L).toInt()
        val estimatedOpens = statsList.count { it.totalTimeInForeground > 30000L }

        return UsageInsight(
            totalScreenTimeMinutes = totalScreenMins,
            totalAppOpens = estimatedOpens.coerceAtLeast(appMinutes.size),
            perAppMinutes = appMinutes
        )
    }
}
