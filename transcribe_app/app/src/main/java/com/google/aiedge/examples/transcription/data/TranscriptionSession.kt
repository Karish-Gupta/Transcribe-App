package com.google.aiedge.examples.transcription.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transcription_sessions")
data class TranscriptionSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val timestamp: Long = System.currentTimeMillis(),

    val content: String
)
