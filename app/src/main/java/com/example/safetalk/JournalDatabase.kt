package com.example.safetalk

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for storing journal entries.
 * It provides an instance of JournalDao for database operations.
 */
@Database(entities = [Journal::class], version = 1, exportSchema = false)
abstract class JournalDatabase : RoomDatabase() {

    // Abstract method to get DAO
    abstract fun journalDao(): JournalDao

    companion object {
        // Volatile annotation ensures visibility of changes across threads
        @Volatile
        private var INSTANCE: JournalDatabase? = null

        /**
         * Get the singleton instance of the database.
         * Creates the database if it does not exist.
         */
        fun getDatabase(context: Context): JournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}