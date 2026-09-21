package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EssentialAppDao {
    @Query("SELECT * FROM essential_apps WHERE isEssential = 1 ORDER BY sortOrder ASC")
    fun getEssentialApps(): Flow<List<EssentialAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setEssential(app: EssentialAppEntity)

    @Query("DELETE FROM essential_apps WHERE packageName = :packageName")
    suspend fun removeEssential(packageName: String)
}

@Dao
interface AppLimitDao {
    @Query("SELECT * FROM app_limits")
    fun getAllLimits(): Flow<List<AppLimitEntity>>

    @Query("SELECT * FROM app_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getLimitForPackage(packageName: String): AppLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(limit: AppLimitEntity)

    @Query("DELETE FROM app_limits WHERE packageName = :packageName")
    suspend fun deleteLimit(packageName: String)
}

@Dao
interface ReadingResultDao {
    @Query("SELECT * FROM reading_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<ReadingResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ReadingResultEntity)

    @Query("DELETE FROM reading_results")
    suspend fun clearHistory()
}

@Dao
interface DropDao {
    @Query("SELECT * FROM drops ORDER BY dropNumber ASC")
    fun getAllDrops(): Flow<List<DropEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDrops(drops: List<DropEntity>)

    @Query("UPDATE drops SET isDiscovered = 1, discoveredAt = :timestamp WHERE dropNumber = :dropNumber")
    suspend fun markDiscovered(dropNumber: String, timestamp: Long)

    @Query("UPDATE drops SET isCompleted = 1 WHERE dropNumber = :dropNumber")
    suspend fun markCompleted(dropNumber: String)
}

@Dao
interface PreferenceDao {
    @Query("SELECT * FROM preferences WHERE key = :key LIMIT 1")
    suspend fun getPreference(key: String): PreferenceEntity?

    @Query("SELECT * FROM preferences")
    fun getAllPreferences(): Flow<List<PreferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(pref: PreferenceEntity)

    @Query("DELETE FROM preferences")
    suspend fun clearAll()
}
