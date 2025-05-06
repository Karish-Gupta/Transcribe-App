package com.google.aiedge.examples.transcription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.aiedge.examples.transcription.data.TranscriptionSession
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : ComponentActivity() {
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                val sessions by viewModel.sessions.collectAsStateWithLifecycle(initialValue = emptyList())

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("History") },
                            actions = {
                                IconButton(onClick = { viewModel.clearAll() }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear all")
                                }
                            }
                        )
                    }
                ) { padding ->
                    if (sessions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No history yet")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            items(sessions) { session ->
                                SessionRow(session)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: TranscriptionSession) {
    val formatted = remember(session.timestamp) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(session.timestamp))
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(formatted, style = MaterialTheme.typography.caption)
            Spacer(Modifier.height(4.dp))
            Text(session.content.take(200), style = MaterialTheme.typography.body1)
        }
    }
}
