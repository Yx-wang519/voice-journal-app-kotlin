package com.example.safetalk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel that provides data to the UI and survives configuration changes.
 * Uses Repository to access the underlying Room database.
 */
class JournalViewModel(application: Application) : AndroidViewModel(application) {

    // Reference to the repository
    private val repository: JournalRepository

    // Public LiveData list of all journal entries
    val allJournals: LiveData<List<Journal>>

    init {
        // Create the database and repository
        val journalDao = JournalDatabase.getDatabase(application).journalDao()
        repository = JournalRepository(journalDao)
        allJournals = repository.allJournals
    }

    /**
     * Launches a coroutine to insert a journal entry into the database.
     */
    fun insert(journal: Journal) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(journal)
    }
}