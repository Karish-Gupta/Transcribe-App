package com.google.aiedge.examples.transcription.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = darkBlue,
            secondary = teal,
            onSurface = teal,
        ),
        content = content
    )
}
