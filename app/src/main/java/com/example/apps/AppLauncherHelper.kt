package com.example.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppLauncherHelper(private val context: Context) {
    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            packageManager.queryIntentActivities(intent, 0)
        }

        val myPackageName = context.packageName

        resolveInfos
            .filter { it.activityInfo.packageName != myPackageName }
            .map { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                val label = resolveInfo.loadLabel(packageManager).toString()
                val iconDrawable = resolveInfo.loadIcon(packageManager)
                val iconBitmap = drawableToBitmap(iconDrawable)
                val category = categorizeApp(pkgName, label)

                val appInfoFlags = resolveInfo.activityInfo.applicationInfo?.flags ?: 0
                val isDownloaded = (appInfoFlags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0 ||
                        (appInfoFlags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

                val versionName = try {
                    val pInfo = packageManager.getPackageInfo(pkgName, 0)
                    pInfo.versionName ?: ""
                } catch (e: Exception) {
                    ""
                }

                AppInfo(
                    packageName = pkgName,
                    activityName = resolveInfo.activityInfo.name,
                    label = label,
                    iconBitmap = iconBitmap,
                    isDownloaded = isDownloaded,
                    category = category,
                    versionName = versionName
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                Toast.makeText(context, "Cannot launch app: $packageName", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to launch: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun categorizeApp(packageName: String, label: String): String {
        val lowerPkg = packageName.lowercase()
        val lowerLabel = label.lowercase()

        return when {
            lowerPkg.contains("dialer") || lowerPkg.contains("phone") || lowerLabel == "phone" -> "COMMUNICATION"
            lowerPkg.contains("messaging") || lowerPkg.contains("mms") || lowerPkg.contains("sms") ||
                    lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") || lowerPkg.contains("signal") -> "COMMUNICATION"
            lowerPkg.contains("camera") || lowerLabel == "camera" -> "TOOLS"
            lowerPkg.contains("maps") || lowerPkg.contains("navigation") || lowerLabel == "maps" -> "NAVIGATION"
            lowerPkg.contains("instagram") || lowerPkg.contains("facebook") || lowerPkg.contains("twitter") ||
                    lowerPkg.contains("tiktok") || lowerPkg.contains("reddit") || lowerPkg.contains("snapchat") -> "SOCIAL"
            lowerPkg.contains("mail") || lowerPkg.contains("gmail") || lowerPkg.contains("outlook") -> "WORK"
            lowerPkg.contains("chrome") || lowerPkg.contains("browser") || lowerPkg.contains("firefox") -> "TOOLS"
            lowerPkg.contains("calc") || lowerPkg.contains("clock") || lowerPkg.contains("calendar") -> "TOOLS"
            lowerPkg.contains("youtube") || lowerPkg.contains("netflix") || lowerPkg.contains("spotify") -> "MEDIA"
            else -> "OTHER"
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 72
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 72

        val bitmap = Bitmap.createBitmap(width.coerceAtMost(96), height.coerceAtMost(96), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
