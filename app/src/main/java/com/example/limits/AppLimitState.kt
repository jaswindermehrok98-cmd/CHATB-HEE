package com.example.limits

data class AppLimitStatus(
    val packageName: String,
    val appLabel: String,
    val usedMinutesToday: Int,
    val dailyLimitMinutes: Int,
    val sessionMinutesRemaining: Int = 0,
    val isSessionActive: Boolean = false,
    val isLimitExceeded: Boolean = false,
    val isEnabled: Boolean = true
)

data class InterventionState(
    val isInterventionActive: Boolean = false,
    val packageName: String = "",
    val appLabel: String = "",
    val usedMinutes: Int = 0,
    val limitMinutes: Int = 0
)
