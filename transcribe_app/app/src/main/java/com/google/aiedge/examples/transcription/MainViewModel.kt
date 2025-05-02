/*
 * Copyright 2024 The Google AI Edge Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.aiedge.examples.transcription

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class MainViewModel() : ViewModel() {

    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return MainViewModel() as T
            }
        }
    }

    private val setting = MutableStateFlow(Setting())
    private val errorMessage = MutableStateFlow<Throwable?>(null)
    private val currentText = MutableStateFlow("Record something for me to transcribe!")

    val uiState: StateFlow<UiState> = combine(
        currentText,
        setting,
        errorMessage
    ) { text, setting, error ->
        UiState(
            currentText = text,
            setting = setting,
            errorMessage = error?.message
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState(currentText = currentText.value, setting = setting.value, errorMessage = errorMessage.value?.message)
    )

    fun setInferenceDevice(device: InferenceDevice) {
        setting.value = setting.value.copy(inferenceDevice = device)
    }

    fun handleRecordedAudio(file: File) {
        if (setting.value.inferenceDevice == InferenceDevice.Cloud) {
            var cloudInference = CloudBasedInference()
            viewModelScope.launch {
                try {
                    val result = cloudInference.sendAudioFile(file)

                    Log.d("MainViewModel", "Result received: $result")
                    currentText.value = result
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error has occurred", e) // Log the actual exception
                    errorMessage.value = e
                }
            }
        }
    }

    fun errorMessageShown() {
        errorMessage.value = null
    }
}

enum class InferenceDevice {
    OnDevice, Cloud
}




//    fun sendAudioToServer(file: File) {
//        viewModelScope.launch {
//            try {
//                val response = audioApi.sendAudio(file) // Replace with your API call
//                uiState.update {
//                    it.copy(transcription = response.text)
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(errorMessage = "Failed to transcribe audio.")
//                }
//            }
//        }
//    }


//    private var lastClassificationTime = 0L
//    private val classificationIntervalMs = 500L // 1/2 second between each request sent
//    fun classify(file: File) {
//        // Implemented request throttling to ensure we do not flood the server with requests
//
//        if (setting.value.inferenceDevice == TranscriptionHelper.InferenceDevice.Cloud) {
//            val currentTime = System.currentTimeMillis()
//            if (currentTime - lastClassificationTime >= classificationIntervalMs) {
//                lastClassificationTime = currentTime
//
//                transcriptionJob = viewModelScope.launch {
//                    try {
//                        val response = audioApi.sendAudio(file) // Replace with your API call
//                        _uiState.update {
//                            it.copy(transcription = response.text)
//                        }
//                    } catch (e: Exception) {
//                        _uiState.update {
//                            it.copy(errorMessage = "Failed to transcribe audio.")
//                        }
//                    }
//                }
//            } else {
//                // Skip classification to avoid flooding server
//            }
//        } else{
//            // Otherwise run on-device classification
//                transcriptionJob = viewModelScope.launch {
////                    transcriptionHelper.classify(
////                        imageProxy.toBitmap(),
////                        imageProxy.imageInfo.rotationDegrees,
////                    )
////                    imageProxy.close()
//                }
//        }
//    }



