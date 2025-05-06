package com.google.aiedge.examples.transcription.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TranscriptionSession::class],
    version = 1,
    exportSchema = false
)
abstract class TranscriptionDatabase : RoomDatabase() {

    abstract fun dao(): TranscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: TranscriptionDatabase? = null

        fun get(context: Context): TranscriptionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TranscriptionDatabase::class.java,
                    "transcription_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
