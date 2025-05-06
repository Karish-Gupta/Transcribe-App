package com.google.aiedge.examples.transcription

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.aiedge.examples.transcription.data.TranscriptionDatabase
import com.google.aiedge.examples.transcription.data.HistoryRepository
import com.google.aiedge.examples.transcription.data.TranscriptionSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = HistoryRepository(
        TranscriptionDatabase.get(application).dao()
    )

    val sessions: Flow<List<TranscriptionSession>> = repo.sessions

    fun clearAll() = viewModelScope.launch {
        repo.clearAll()
    }
}
