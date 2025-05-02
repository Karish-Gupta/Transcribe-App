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
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * This class sets up a client and connects to the proper server URL
 * Defines the functions necessary to send POST requests to the server
 */
class CloudBasedInference {

    private val SERVER_URL = "http://10.0.2.2:5050/predict"
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Time to establish connection
        .readTimeout(30, TimeUnit.SECONDS)    // Time to wait for server response
        .writeTimeout(30, TimeUnit.SECONDS)   // Time allowed to send data to server
        .build()

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
        Log.d("Cloud Inference", "Sending POST request")
        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.d("Cloud Inference", "Failed response")
                    throw IOException("Unexpected response: ${response.code}")
                }
                // Read the response body once and parse it
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")

                // Log the full response
                Log.d("Cloud Inference", "Successful response: $responseBody")

                // Parse the JSON and extract the transcription
                val jsonResponse = JSONObject(responseBody)
                val transcription = jsonResponse.getString("transcription")

                // Log the extracted transcription for debugging
                Log.d("Cloud Inference", "Transcription: $transcription")

                // Return the transcription (without the surrounding JSON structure)
                return@use transcription
            }
        }
    }
}