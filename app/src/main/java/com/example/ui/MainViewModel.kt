package com.example.ui

import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apps.AppInfo
import com.example.apps.AppLauncherHelper
import com.example.content.DropDetail
import com.example.content.DropsEngine
import com.example.content.ReadResultProfile
import com.example.content.TheReadEngine
import com.example.data.AppLimitEntity
import com.example.data.DropEntity
import com.example.data.ReadingResultEntity
import com.example.data.UnplugDatabase
import com.example.data.UnplugRepository
import com.example.device.DeviceLockManager
import com.example.device.DeviceTimerState
import com.example.focus.FocusSessionManager
import com.example.focus.FocusState
import com.example.limits.AppLimitStatus
import com.example.limits.InterventionState
import com.example.limits.LimitsManager
import com.example.limits.UsageInsight
import com.example.limits.UsageStatsHelper
import com.example.message.DailyMessage
import com.example.message.TodayMessageEngine
import com.example.notifications.NotificationDigest
import com.example.notifications.NotificationRepository
import com.example.notifications.UnplugNotificationItem
import com.example.focus.BinauralSynthesizer
import com.example.focus.SoundPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    INBOX,
    APP_DRAWER,
    LIMITS,
    FOCUS,
    DEVICE_LOCK,
    THE_READ,
    DROPS,
    LAB,
    FILES,
    ARCHIVE,
    SYSTEM,
    ONBOARDING,
    PREMIUM,
    SETUP
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val database = UnplugDatabase.getDatabase(context)
    val repository = UnplugRepository(database)
    val appLauncherHelper = AppLauncherHelper(context)
    val notificationRepository = NotificationRepository(context)
    val usageStatsHelper = UsageStatsHelper(context)
    val limitsManager = LimitsManager(context, repository, usageStatsHelper)
    val focusSessionManager = FocusSessionManager(context)
    val deviceLockManager = DeviceLockManager(context)
    val binauralSynthesizer = BinauralSynthesizer()

    // Navigation State
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Premium Unlocked State (Persistent via Room Preferences)
    private val _isPremiumUnlocked = MutableStateFlow(false)
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked.asStateFlow()

    // Sound Synthesizer State (Pro Feature)
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()
    private val _activeAudioPreset = MutableStateFlow(SoundPreset.SOLFEGGIO_432)
    val activeAudioPreset: StateFlow<SoundPreset> = _activeAudioPreset.asStateFlow()

    // Pro Theme State
    private val _proTheme = MutableStateFlow("OLED_BLACK")
    val proTheme: StateFlow<String> = _proTheme.asStateFlow()

    // App Drawer Filter & Mode
    private val _appFilterCategory = MutableStateFlow("ALL")
    val appFilterCategory: StateFlow<String> = _appFilterCategory.asStateFlow()

    private val _appViewMode = MutableStateFlow("LIST") // "LIST" or "GRID"
    val appViewMode: StateFlow<String> = _appViewMode.asStateFlow()

    // Pro Vault Hiding
    private val _vaultHiddenPackages = MutableStateFlow<Set<String>>(emptySet())
    val vaultHiddenPackages: StateFlow<Set<String>> = _vaultHiddenPackages.asStateFlow()

    // Apps State
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val allApps: StateFlow<List<AppInfo>> = _allApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Daily Message
    private val _messageIndexOverride = MutableStateFlow<Int?>(null)
    private val _todayMessage = MutableStateFlow(TodayMessageEngine.getMessageForCurrentContext())
    val todayMessage: StateFlow<DailyMessage> = _todayMessage.asStateFlow()

    // Notifications
    val notifications: StateFlow<List<UnplugNotificationItem>> = notificationRepository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationDigest: StateFlow<NotificationDigest> = notificationRepository.digest
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationDigest(0, 0, 0, 0, 0, 0, 0))

    // Limits & Usage
    val interventionState: StateFlow<InterventionState> = limitsManager.interventionState
    private val _usageInsight = MutableStateFlow(UsageInsight(0, 0, emptyMap()))
    val usageInsight: StateFlow<UsageInsight> = _usageInsight.asStateFlow()

    val appLimits: StateFlow<List<AppLimitEntity>> = repository.appLimits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Focus & Device Lock
    val focusState: StateFlow<FocusState> = focusSessionManager.focusState
    val deviceTimerState: StateFlow<DeviceTimerState> = deviceLockManager.timerState

    // Drops & The Read
    val drops: StateFlow<List<DropEntity>> = repository.drops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDrop = MutableStateFlow<DropDetail?>(null)
    val selectedDrop: StateFlow<DropDetail?> = _selectedDrop.asStateFlow()

    // The Read active flow
    private val _readStep = MutableStateFlow(0)
    val readStep: StateFlow<Int> = _readStep.asStateFlow()
    private val _readAnswers = MutableStateFlow<List<Int>>(emptyList())
    val readAnswers: StateFlow<List<Int>> = _readAnswers.asStateFlow()
    private val _readResult = MutableStateFlow<ReadResultProfile?>(null)
    val readResult: StateFlow<ReadResultProfile?> = _readResult.asStateFlow()

    // Central synchronized Time Flow (updates every second)
    private val _systemTime = MutableStateFlow(System.currentTimeMillis())
    val systemTime: StateFlow<Long> = _systemTime.asStateFlow()

    // Persistent clock offset in milliseconds
    private val _timeOffsetMs = MutableStateFlow(0L)
    val timeOffsetMs: StateFlow<Long> = _timeOffsetMs.asStateFlow()

    // Settings / Preferences
    private val _reducedMotion = MutableStateFlow(false)
    val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDropsIfEmpty()
            loadApps()
            refreshUsage()
            checkOnboarding()
            checkPremiumStatus()

            // Load saved time offset (defaulting to 32 minutes offset to align with 8:33 local time)
            val offsetStr = repository.getPreference("CLOCK_TIME_OFFSET_MS", "1920000")
            val savedOffset = offsetStr.toLongOrNull() ?: 0L
            _timeOffsetMs.value = savedOffset

            // Start clock ticker
            while (true) {
                _systemTime.value = System.currentTimeMillis() + _timeOffsetMs.value
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private suspend fun checkPremiumStatus() {
        val status = repository.getPreference("PREMIUM_UNLOCKED_STATUS", "false")
        _isPremiumUnlocked.value = (status == "true")
        val theme = repository.getPreference("PRO_THEME", "OLED_BLACK")
        _proTheme.value = theme
    }

    fun unlockPremiumWithCode(inputCode: String): Boolean {
        val trimmed = inputCode.trim()
        if (trimmed == "2013") {
            viewModelScope.launch {
                repository.setPreference("PREMIUM_UNLOCKED_STATUS", "true")
                _isPremiumUnlocked.value = true
            }
            return true
        }
        return false
    }

    // Audio Synthesizer (Premium feature)
    fun toggleAudio(preset: SoundPreset = _activeAudioPreset.value) {
        if (!_isPremiumUnlocked.value) return
        if (_isAudioPlaying.value && _activeAudioPreset.value == preset) {
            stopAudio()
        } else {
            _activeAudioPreset.value = preset
            _isAudioPlaying.value = true
            binauralSynthesizer.play(preset, viewModelScope)
        }
    }

    fun stopAudio() {
        _isAudioPlaying.value = false
        binauralSynthesizer.stop()
    }

    fun setProTheme(theme: String) {
        if (!_isPremiumUnlocked.value) return
        _proTheme.value = theme
        viewModelScope.launch {
            repository.setPreference("PRO_THEME", theme)
        }
    }

    // App Drawer Categorization & Modes
    fun setAppFilter(category: String) {
        _appFilterCategory.value = category
    }

    fun toggleAppViewMode() {
        _appViewMode.value = if (_appViewMode.value == "LIST") "GRID" else "LIST"
    }

    fun toggleVaultApp(packageName: String) {
        if (!_isPremiumUnlocked.value) return
        val current = _vaultHiddenPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _vaultHiddenPackages.value = current
    }

    override fun onCleared() {
        super.onCleared()
        binauralSynthesizer.stop()
    }

    private suspend fun checkOnboarding() {
        val onboarded = repository.getPreference("ONBOARDING_COMPLETED", "false")
        if (onboarded == "false") {
            _currentScreen.value = Screen.ONBOARDING
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setPreference("ONBOARDING_COMPLETED", "true")
            _currentScreen.value = Screen.SETUP
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun loadApps() {
        viewModelScope.launch {
            val apps = appLauncherHelper.getInstalledApps()
            _allApps.value = apps
            refreshUsage()
        }
    }

    fun refreshUsage() {
        val usage = usageStatsHelper.getTodayUsage()
        _usageInsight.value = usage
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun executeSearchOrCommand(query: String): Boolean {
        val trimmed = query.trim()
        val lower = trimmed.lowercase()

        // Deterministic commands
        if (lower.startsWith("focus ") || lower.startsWith("focus")) {
            val mins = lower.replace("focus", "").trim().toIntOrNull() ?: 25
            focusSessionManager.startFocus(mins)
            _searchQuery.value = ""
            _currentScreen.value = Screen.HOME
            return true
        }

        if (lower.startsWith("lock in ") || lower.startsWith("lock ")) {
            val mins = lower.replace("lock in", "").replace("lock", "").trim().toIntOrNull() ?: 30
            deviceLockManager.startTimer(mins)
            _searchQuery.value = ""
            _currentScreen.value = Screen.HOME
            return true
        }

        if (lower == "lock now" || lower == "lock") {
            deviceLockManager.lockNow()
            _searchQuery.value = ""
            return true
        }

        if (lower == "inbox") {
            _currentScreen.value = Screen.INBOX
            _searchQuery.value = ""
            return true
        }

        // Match single app
        val match = _allApps.value.firstOrNull {
            it.label.equals(trimmed, ignoreCase = true) || it.label.lowercase().startsWith(lower)
        }
        if (match != null) {
            launchApp(match.packageName, match.label)
            _searchQuery.value = ""
            return true
        }

        return false
    }

    fun launchApp(packageName: String, label: String) {
        viewModelScope.launch {
            val allowed = limitsManager.checkAppLaunchAllowed(packageName, label)
            if (allowed) {
                appLauncherHelper.launchApp(packageName)
            }
        }
    }

    fun cycleTodayMessage() {
        val currentIdx = _messageIndexOverride.value ?: 0
        val nextIdx = currentIdx + 1
        _messageIndexOverride.value = nextIdx
        _todayMessage.value = TodayMessageEngine.getMessageForCurrentContext(
            isFocusActive = focusState.value.isActive,
            overrideIndex = nextIdx
        )
    }

    // Limits
    fun setAppLimit(packageName: String, dailyMinutes: Int) {
        viewModelScope.launch {
            repository.saveLimit(packageName, dailyMinutes)
        }
    }

    fun removeAppLimit(packageName: String) {
        viewModelScope.launch {
            repository.removeLimit(packageName)
        }
    }

    fun startSessionLimit(packageName: String, minutes: Int) {
        limitsManager.startSession(packageName, minutes)
    }

    fun addInterventionOverride(packageName: String, extraMinutes: Int = 5) {
        limitsManager.addExtraMinutes(packageName, extraMinutes)
    }

    fun dismissIntervention() {
        limitsManager.dismissIntervention()
    }

    // Notifications
    fun replyToNotification(key: String, text: String): Boolean {
        return notificationRepository.reply(key, text)
    }

    fun openNotification(key: String): Boolean {
        return notificationRepository.openNotification(key)
    }

    fun dismissNotification(key: String): Boolean {
        return notificationRepository.dismissNotification(key)
    }

    fun clearAllNotifications(): Boolean {
        return notificationRepository.clearAll()
    }

    // The Read
    fun startTheRead() {
        _readStep.value = 0
        _readAnswers.value = emptyList()
        _readResult.value = null
        _currentScreen.value = Screen.THE_READ
    }

    fun answerReadQuestion(weight: Int) {
        val nextAnswers = _readAnswers.value + weight
        _readAnswers.value = nextAnswers
        if (nextAnswers.size >= TheReadEngine.questions.size) {
            val result = TheReadEngine.calculateResult(nextAnswers)
            _readResult.value = result
            viewModelScope.launch {
                repository.saveReadingResult(
                    ReadingResultEntity(
                        profileTitle = result.title,
                        archetype = result.archetype,
                        summary = result.summary,
                        choicesSummary = "Score: ${result.score}/15"
                    )
                )
            }
        } else {
            _readStep.value = _readStep.value + 1
        }
    }

    // Drops
    fun openDrop(dropNumber: String) {
        _selectedDrop.value = DropsEngine.getDrop(dropNumber)
        _currentScreen.value = Screen.DROPS
        viewModelScope.launch {
            repository.discoverDrop(dropNumber)
        }
    }

    fun completeCurrentDrop(dropNumber: String) {
        viewModelScope.launch {
            repository.completeDrop(dropNumber)
            _selectedDrop.value = null
        }
    }

    // Preferences
    fun toggleReducedMotion() {
        _reducedMotion.value = !_reducedMotion.value
    }

    fun toggleHaptics() {
        _hapticsEnabled.value = !_hapticsEnabled.value
    }

    fun setTimeOffset(offsetMs: Long) {
        _timeOffsetMs.value = offsetMs
        viewModelScope.launch {
            repository.setPreference("CLOCK_TIME_OFFSET_MS", offsetMs.toString())
        }
    }

    fun adjustTimeOffsetMinutes(minutes: Int) {
        val currentOffset = _timeOffsetMs.value
        val newOffset = currentOffset + (minutes * 60L * 1000L)
        setTimeOffset(newOffset)
    }

    fun startActivitySafely(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // System Intents
    fun getHomeRoleIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
            } else {
                Intent(Settings.ACTION_HOME_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }
        } else {
            Intent(Settings.ACTION_HOME_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        }
    }

    fun isHomeApp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                return roleManager.isRoleHeld(RoleManager.ROLE_HOME)
            }
        }
        return false
    }
}
