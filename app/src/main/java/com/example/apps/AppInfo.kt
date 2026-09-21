package com.example.apps

import android.graphics.Bitmap

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconBitmap: Bitmap? = null,
    val isEssential: Boolean = false,
    val isDownloaded: Boolean = true,
    val category: String = "GENERAL",
    val usageTodayMinutes: Int = 0,
    val dailyLimitMinutes: Int = 0,
    val launchCount: Int = 0,
    val versionName: String = ""
)

data class InstalledAppResult(
    val apps: List<AppInfo>,
    val essentialApps: List<AppInfo>,
    val recentApps: List<AppInfo>
)

