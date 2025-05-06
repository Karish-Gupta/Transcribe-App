package com.google.aiedge.examples.transcription.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDao {
    @Query("SELECT * FROM transcription_sessions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TranscriptionSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TranscriptionSession)

    @Query("DELETE FROM transcription_sessions")
    suspend fun clearAll()
}
