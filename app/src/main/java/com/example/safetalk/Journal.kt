package com.example.safetalk

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a journal entry.
 * Each entry contains transcribed text, location (city), timestamp, and weather info.
 */
@Entity(tableName = "journal_table")
data class Journal(

    // Primary key that auto-generates a unique ID for each entry
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Transcribed speech text
    val text: String,

    // City retrieved from location services
    val city: String,

    // Timestamp in string format (e.g., "2025-06-07 12:34:56")
    val timestamp: String,

    // New field: Weather info, e.g., "Cloudy, 17°C"
    //val weather: String = "Unknown"
)