package com.example.data

import kotlinx.coroutines.flow.Flow

class UnplugRepository(private val database: UnplugDatabase) {
    val essentialApps: Flow<List<EssentialAppEntity>> = database.essentialAppDao().getEssentialApps()
    val appLimits: Flow<List<AppLimitEntity>> = database.appLimitDao().getAllLimits()
    val readingResults: Flow<List<ReadingResultEntity>> = database.readingResultDao().getAllResults()
    val drops: Flow<List<DropEntity>> = database.dropDao().getAllDrops()
    val preferences: Flow<List<PreferenceEntity>> = database.preferenceDao().getAllPreferences()

    suspend fun setEssentialApp(packageName: String, isEssential: Boolean, sortOrder: Int = 0) {
        if (isEssential) {
            database.essentialAppDao().setEssential(
                EssentialAppEntity(packageName = packageName, isEssential = true, sortOrder = sortOrder)
            )
        } else {
            database.essentialAppDao().removeEssential(packageName)
        }
    }

    suspend fun getLimit(packageName: String): AppLimitEntity? {
        return database.appLimitDao().getLimitForPackage(packageName)
    }

    suspend fun saveLimit(packageName: String, dailyMinutes: Int, sessionMinutes: Int = 10, isEnabled: Boolean = true) {
        database.appLimitDao().insertOrUpdate(
            AppLimitEntity(
                packageName = packageName,
                dailyLimitMinutes = dailyMinutes,
                sessionLimitMinutes = sessionMinutes,
                isEnabled = isEnabled,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeLimit(packageName: String) {
        database.appLimitDao().deleteLimit(packageName)
    }

    suspend fun saveReadingResult(result: ReadingResultEntity) {
        database.readingResultDao().insertResult(result)
    }

    suspend fun clearReadingHistory() {
        database.readingResultDao().clearHistory()
    }

    suspend fun seedInitialDropsIfEmpty() {
        val initialDrops = listOf(
            DropEntity(
                dropNumber = "DROP 001",
                title = "THE WAIT",
                category = "PERCEPTION",
                description = "Thirty seconds of intentional stillness before entering any social feed. Feel the impulse dissolve.",
                isDiscovered = true
            ),
            DropEntity(
                dropNumber = "DROP 002",
                title = "THE CHOICE",
                category = "DECISION",
                description = "Every app opened is a silent trade of conscious time. What did you trade for this session?",
                isDiscovered = true
            ),
            DropEntity(
                dropNumber = "DROP 003",
                title = "THE SIGNAL",
                category = "ATTENTION",
                description = "Distinguishing genuine human communications from algorithmic bait. Filter the frequency.",
                isDiscovered = false
            ),
            DropEntity(
                dropNumber = "DROP 004",
                title = "THE SILENCE",
                category = "RESET",
                description = "A phone without pending red badges is not empty. It is spacious and calm.",
                isDiscovered = false
            ),
            DropEntity(
                dropNumber = "DROP 005",
                title = "THE THRESHOLD",
                category = "SYSTEM",
                description = "The physical edge between the glass rectangle in your hand and the room you are standing in.",
                isDiscovered = false
            )
        )
        database.dropDao().insertDrops(initialDrops)
    }

    suspend fun discoverDrop(dropNumber: String) {
        database.dropDao().markDiscovered(dropNumber, System.currentTimeMillis())
    }

    suspend fun completeDrop(dropNumber: String) {
        database.dropDao().markCompleted(dropNumber)
    }

    suspend fun getPreference(key: String, defaultValue: String): String {
        return database.preferenceDao().getPreference(key)?.value ?: defaultValue
    }

    suspend fun setPreference(key: String, value: String) {
        database.preferenceDao().setPreference(PreferenceEntity(key, value))
    }
}
