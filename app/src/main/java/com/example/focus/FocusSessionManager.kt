package com.example.focus

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class FocusState(
    val isActive: Boolean = false,
    val totalMinutes: Int = 0,
    val remainingSeconds: Int = 0,
    val isCompleted: Boolean = false
)

class FocusSessionManager(private val context: Context) {
    private val _focusState = MutableStateFlow(FocusState())
    val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var focusJob: Job? = null

    fun startFocus(minutes: Int) {
        focusJob?.cancel()
        val totalSecs = minutes * 60
        _focusState.value = FocusState(
            isActive = true,
            totalMinutes = minutes,
            remainingSeconds = totalSecs,
            isCompleted = false
        )

        focusJob = scope.launch {
            var left = totalSecs
            while (isActive && left > 0) {
                delay(1000)
                left--
                _focusState.value = _focusState.value.copy(remainingSeconds = left)
            }

            if (left <= 0) {
                vibrateCompletion()
                _focusState.value = _focusState.value.copy(
                    isActive = false,
                    isCompleted = true,
                    remainingSeconds = 0
                )
            }
        }
    }

    fun endFocus() {
        focusJob?.cancel()
        _focusState.value = FocusState()
    }

    private fun vibrateCompletion() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 250), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 150, 100, 250), -1)
            }
        } catch (e: Exception) {
            // Ignore if vibration fails
        }
    }
}
