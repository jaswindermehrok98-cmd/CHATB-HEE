package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "essential_apps")
data class EssentialAppEntity(
    @PrimaryKey val packageName: String,
    val isEssential: Boolean = true,
    val sortOrder: Int = 0,
    val customCategory: String = "ESSENTIAL"
)

@Entity(tableName = "app_limits")
data class AppLimitEntity(
    @PrimaryKey val packageName: String,
    val dailyLimitMinutes: Int = 15,
    val sessionLimitMinutes: Int = 10,
    val isEnabled: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_results")
data class ReadingResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val profileTitle: String,
    val archetype: String,
    val summary: String,
    val choicesSummary: String
)

@Entity(tableName = "drops")
data class DropEntity(
    @PrimaryKey val dropNumber: String,
    val title: String,
    val category: String,
    val description: String,
    val isDiscovered: Boolean = false,
    val isCompleted: Boolean = false,
    val discoveredAt: Long = 0L
)

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)
