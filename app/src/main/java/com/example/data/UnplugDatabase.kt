package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EssentialAppEntity::class,
        AppLimitEntity::class,
        ReadingResultEntity::class,
        DropEntity::class,
        PreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UnplugDatabase : RoomDatabase() {
    abstract fun essentialAppDao(): EssentialAppDao
    abstract fun appLimitDao(): AppLimitDao
    abstract fun readingResultDao(): ReadingResultDao
    abstract fun dropDao(): DropDao
    abstract fun preferenceDao(): PreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: UnplugDatabase? = null

        fun getDatabase(context: Context): UnplugDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UnplugDatabase::class.java,
                    "unplug_offline_os.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
