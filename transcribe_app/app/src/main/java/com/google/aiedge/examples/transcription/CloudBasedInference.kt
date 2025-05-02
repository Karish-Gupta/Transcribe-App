package com.google.aiedge.examples.transcription

import android.graphics.Bitmap
import android.util.Log
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * This class sets up a client and connects to the proper server URL
 * Defines the functions necessary to send POST requests to the server
 */
class CloudBasedInference {

    private val SERVER_URL = "http://10.0.2.2:5050/predict"
    private val client = OkHttpClient()

    suspend fun sendAudioFile(file: File): String {
        // Build the request body with the file
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "file", // must match what server expects
                filename = file.name,
                body = file.asRequestBody("audio/wav".toMediaType())
            )
            .build()

        // Build the request
        val request = Request.Builder()
            .url(SERVER_URL)
            .post(requestBody)
            .build()

        // Execute request in coroutine context
        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.d("Cloud Inference", "Failed response")
                    throw IOException("Unexpected response: ${response.code}")
                }
                Log.d("Cloud Inference", "Successful response")
                response.body?.string() ?: throw IOException("Empty response body")
            }
        }
    }
}