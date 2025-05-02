package com.google.aiedge.examples.transcription.view

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.aiedge.examples.transcription.UiState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.google.aiedge.examples.transcription.AudioRecorder
import java.io.File
import kotlin.random.Random


@Composable
fun AudioScreen(
    uiState: UiState,
    modifier: Modifier = Modifier,
    onAudioInput: (File) -> Unit, // now passes a File to ViewModel
) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder() }
    var isRecording by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission is denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request microphone permission on launch
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        MicrophoneButton(onClick = {
            if (!isRecording) {
                val file = audioRecorder.startRecording()
                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                isRecording = true
            } else {
                val file = audioRecorder.stopRecording()
                Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                isRecording = false
                file?.let { onAudioInput(it) } // Send the file to ViewModel
            }
        })
    }
}

// Audio feedback display
@Composable
fun AudioFeedbackDisplay(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    volumeLevel: Float // normalized 0f to 1f
) {
    val bars = 10
    val levels = remember { List(bars) { Random.nextFloat() } }
    val animatedLevels = levels.map {
        animateFloatAsState(
            targetValue = if (isRecording) it * volumeLevel else 0f,
            animationSpec = tween(durationMillis = 100)
        )
    }

    Row(
        modifier = modifier
            .height(50.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        animatedLevels.forEach { animatedLevel ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(6.dp)
                    .height((animatedLevel.value * 50).dp)
                    .background(MaterialTheme.colors.primary, RoundedCornerShape(3.dp))
            )
        }
    }
}


@Composable
fun MicrophoneButton(onClick: () -> Unit) {
    var isClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
    ) {
        Button(
            onClick = {
                isClicked = !isClicked // Toggle the state
                onClick()
            },
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isClicked) Color.Red else Color.LightGray
            )
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Microphone",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AudioFeedbackDisplay(
            isRecording = isClicked,
            volumeLevel = 0.6f // Replace with actual mic input later
        )
    }
}
