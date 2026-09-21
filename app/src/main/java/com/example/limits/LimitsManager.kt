package com.example.limits

import android.content.Context
import com.example.data.AppLimitEntity
import com.example.data.UnplugRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LimitsManager(
    private val context: Context,
    private val repository: UnplugRepository,
    private val usageStatsHelper: UsageStatsHelper
) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val _interventionState = MutableStateFlow(InterventionState())
    val interventionState: StateFlow<InterventionState> = _interventionState.asStateFlow()

    // PackageName -> Session expiration timestamp
    private val sessionExpirations = mutableMapOf<String, Long>()

    // PackageName -> Additional granted minutes for today
    private val extraMinutesGranted = mutableMapOf<String, Int>()

    init {
        startSessionMonitor()
    }

    private fun startSessionMonitor() {
        scope.launch {
            while (isActive) {
                delay(10000) // check every 10 seconds
                val now = System.currentTimeMillis()
                sessionExpirations.entries.removeAll { it.value <= now }
            }
        }
    }

    fun startSession(packageName: String, minutes: Int) {
        sessionExpirations[packageName] = System.currentTimeMillis() + (minutes * 60 * 1000L)
    }

    fun addExtraMinutes(packageName: String, minutes: Int = 5) {
        val current = extraMinutesGranted[packageName] ?: 0
        extraMinutesGranted[packageName] = current + minutes
        // Also extend session
        sessionExpirations[packageName] = System.currentTimeMillis() + (minutes * 60 * 1000L)
        dismissIntervention()
    }

    fun dismissIntervention() {
        _interventionState.value = InterventionState(isInterventionActive = false)
    }

    fun triggerIntervention(packageName: String, appLabel: String, used: Int, limit: Int) {
        _interventionState.value = InterventionState(
            isInterventionActive = true,
            packageName = packageName,
            appLabel = appLabel,
            usedMinutes = used,
            limitMinutes = limit
        )
    }

    suspend fun checkAppLaunchAllowed(packageName: String, appLabel: String): Boolean {
        // If session active, allow
        val sessionExpiry = sessionExpirations[packageName]
        if (sessionExpiry != null && sessionExpiry > System.currentTimeMillis()) {
            return true
        }

        val configuredLimit = repository.getLimit(packageName)

        if (configuredLimit == null || !configuredLimit.isEnabled) {
            return true
        }

        val usage = usageStatsHelper.getTodayUsage()
        val used = usage.perAppMinutes[packageName] ?: 0
        val extra = extraMinutesGranted[packageName] ?: 0
        val effectiveLimit = configuredLimit.dailyLimitMinutes + extra

        if (used >= effectiveLimit) {
            triggerIntervention(packageName, appLabel, used, effectiveLimit)
            return false
        }

        return true
    }
}
