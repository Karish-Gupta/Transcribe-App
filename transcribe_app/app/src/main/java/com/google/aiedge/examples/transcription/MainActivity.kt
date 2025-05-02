package com.google.aiedge.examples.transcription

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.aiedge.examples.transcription.view.ApplicationTheme
import com.google.aiedge.examples.transcription.view.AudioScreen
import java.io.File
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels { MainViewModel.getFactory(this) }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.errorMessageShown()
                }
            }

            ApplicationTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Header() // Top app bar

                    // Main content area (transcription display), takes up all available space
                    TranscriptionDisplay(
                        uiState = uiState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    // Bottom controls section
                    BottomSection(
                        uiState = uiState,
                        onInferenceDeviceSelected = {
                            viewModel.setInferenceDevice(it)
                        },
                        onAudioInput = {
                            viewModel.handleRecordedAudio(it)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun Header() {
        TopAppBar(
            backgroundColor = Color.LightGray,
            elevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Transcribe",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
        }
    }

    // Text generation section
    @Composable
    fun TranscriptionDisplay(
        uiState: UiState,
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            // Scrollable top section with generated text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = uiState.currentText,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray)
            )
        }
    }


    // Organize control panel (bottom half of screen
    @Composable
    fun BottomSection(
        uiState: UiState,
        modifier: Modifier = Modifier,
        onInferenceDeviceSelected: (InferenceDevice) -> Unit, // On inference device
        onAudioInput: (File) -> Unit
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = modifier.padding(horizontal = 20.dp)
        ) {
            // Microphone component
            AudioScreen(
                uiState = uiState,
                onAudioInput = {
                    onAudioInput(it)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Inference UI component
            InferenceDevice(onInferenceDeviceSelected = {
                onInferenceDeviceSelected(it) // inference callback
            })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Inference device composable function
    @Composable
    fun InferenceDevice(
        modifier: Modifier = Modifier,
        onInferenceDeviceSelected: (InferenceDevice) -> Unit
    ) {
        val options = listOf("On-Device", "Cloud")
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(options.first()) }

        Box(modifier = modifier) {
            Column {
                Text(
                    text = "Inference Mode:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clickable { expanded = true }
                ) {
                    Text(text = selectedOption)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedOption = option
                            expanded = false
                            val device = when (option) {
                                "Cloud" -> InferenceDevice.Cloud
                                else -> InferenceDevice.OnDevice
                            }
                            onInferenceDeviceSelected(device)
                        }) {
                            Text(text = option)
                        }
                    }
                }
            }
        }
    }

}