package com.example.device

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class DeviceTimerState(
    val isActive: Boolean = false,
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0
)

class DeviceLockManager(private val context: Context) {
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent = ComponentName(context, UnplugDeviceAdminReceiver::class.java)

    private val _timerState = MutableStateFlow(DeviceTimerState())
    val timerState: StateFlow<DeviceTimerState> = _timerState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    fun getDeviceAdminIntent(): Intent {
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Enables UNPLUG LABS OFFLINE OS to lock the screen when device timers expire."
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun startTimer(minutes: Int) {
        timerJob?.cancel()
        val totalSecs = minutes * 60
        _timerState.value = DeviceTimerState(
            isActive = true,
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs
        )

        timerJob = scope.launch {
            var left = totalSecs
            while (isActive && left > 0) {
                delay(1000)
                left--
                _timerState.value = _timerState.value.copy(remainingSeconds = left)
            }

            if (left <= 0) {
                triggerLock()
            }
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _timerState.value = DeviceTimerState(isActive = false, totalSeconds = 0, remainingSeconds = 0)
    }

    fun lockNow(): Boolean {
        return triggerLock()
    }

    private fun triggerLock(): Boolean {
        vibrateCompletion()
        _timerState.value = DeviceTimerState(isActive = false, totalSeconds = 0, remainingSeconds = 0)

        return if (isDeviceAdminActive()) {
            try {
                devicePolicyManager.lockNow()
                true
            } catch (e: Exception) {
                Toast.makeText(context, "Lock failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(
                context,
                "Device Lock requires Device Admin permission. Enable it in Settings.",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }

    private fun vibrateCompletion() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(400)
            }
        } catch (e: Exception) {
            // Ignore if vibration fails
        }
    }
}
