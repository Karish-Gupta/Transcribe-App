package com.google.aiedge.examples.transcription.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: TranscriptionDao) {
    val sessions: Flow<List<TranscriptionSession>> = dao.getAll()

    suspend fun add(content: String) {
        dao.insert(TranscriptionSession(content = content))
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
