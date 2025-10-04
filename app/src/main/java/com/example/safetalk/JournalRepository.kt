package com.example.safetalk

import androidx.lifecycle.LiveData

/**
 * Repository that abstracts access to journal data sources.
 * Provides a clean API for ViewModel to access data.
 */
class JournalRepository(private val journalDao: JournalDao) {

    /**
     * LiveData list of all journal entries.
     * Observed in the UI to reflect changes in real-time.
     */
    val allJournals: LiveData<List<Journal>> = journalDao.getAllJournals()

    /**
     * Inserts a journal entry into the database.
     * Called from ViewModel in a coroutine scope.
     */
    suspend fun insert(journal: Journal) {
        journalDao.insert(journal)
    }
}