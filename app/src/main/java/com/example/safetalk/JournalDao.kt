package com.example.safetalk

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO interface for accessing journal entries in the Room database.
 */
@Dao
interface JournalDao {

    /**
     * Insert a new journal entry into the database.
     * If there's a conflict (same ID), the old entry will be replaced.
     */
    @Insert
    suspend fun insert(journal: Journal)

    /**
     * Get all journal entries, ordered by newest first.
     * Returns LiveData for automatic updates in UI.
     */
    @Query("SELECT * FROM journal_table ORDER BY id DESC")
    fun getAllJournals(): LiveData<List<Journal>>
}