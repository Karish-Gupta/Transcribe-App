package com.google.aiedge.examples.imageclassification

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.LifecycleOwner
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * This class sets up a client and connects to the proper server URL
 * Defines the functions necessary to send POST requests to the server
 */
class CloudBasedInference {

    private val SERVER_URL = "http://10.0.2.2:5050/predict" // Set server URL
    private val client = OkHttpClient.Builder().build() // Initialize client with OkHttpClient API

    /**
     * Takes in bitmap data of a single frame
     *
     */
    suspend fun sendDataToServer(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        // Converting the Bitmap data to a JPEG byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()


        val mediaType = "image/jpeg".toMediaTypeOrNull() // Set media type
        val imageRequestBody = byteArray.toRequestBody(mediaType) // Set request body with media type

        // Set request body with proper type and form data section
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "frame.jpg", imageRequestBody)
            .build()

        // Define POST request to server
        val request = Request.Builder()
            .url(SERVER_URL)
            .post(requestBody)
            .build()

        // Send POST request
        val response = client.newCall(request).execute()

        // Error handling, throw an exception
        if (!response.isSuccessful) {
            throw IOException("Error: ${response.code}")
        }

        // Return response body or throw an exception if the body is empty
        return@withContext response.body?.string() ?: throw IOException("Empty response body")
    }

    data class Prediction(val label: String, val score: Float) // Prediction contains a label and a confidence value
    data class PredictionResponse(val predictions: List<Prediction>) // List of predictions
}
