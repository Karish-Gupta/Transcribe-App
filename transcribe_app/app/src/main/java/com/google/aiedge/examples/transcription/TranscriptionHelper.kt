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
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.metadata.MetadataExtractor
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class TranscriptionHelper(private val context: Context) {


    /**
     * A separate classify function that calls function to classify with the cloud model
     * Passes bitmap data to the sendDataToServer function, which then returns a response to the POST request sent
     */
//    suspend fun classifyWithServer(bitmap: Bitmap) {
//        try {
//            val startTime = System.currentTimeMillis() // Track inference time
//            val cloudRequest = CloudBasedInference()
//            val jsonResponse = cloudRequest.sendDataToServer(bitmap) // Send POST request to server and response will be returned
//
//            val response = Gson().fromJson(jsonResponse, PredictionResponse::class.java) // Deserialize JSON response
//            // Get to N results
//            val categories = response.predictions
//                .map { Category(label = it.label, score = it.score) }
//                .sortedByDescending { it.score }
//                .take(options.resultCount)
//
//            val inferenceTime = System.currentTimeMillis() - startTime // Inference time result
//            _classification.emit(ClassificationResult(categories, inferenceTime)) // Emit data
//
//            // Error handling
//        } catch (e: Exception) {
//            Log.i(TAG, "Image classification error occurred: ${e.message}")
//            _error.emit(e)
//        }
//
//    }

//    suspend fun classify(bitmap: Bitmap, rotationDegrees: Int) {
//        try {
//            withContext(Dispatchers.IO) {
//                if (interpreter == null) return@withContext
//                val startTime = SystemClock.uptimeMillis()
//
//                val rotation = -rotationDegrees / 90
//                val (_, h, w, _) = interpreter?.getInputTensor(0)?.shape() ?: return@withContext
//                val imageProcessor =
//                    ImageProcessor.Builder().add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
//                        .add(Rot90Op(rotation)).add(NormalizeOp(127.5f, 127.5f)).build()
//
//                // Preprocess the image and convert it into a TensorImage for classification.
//                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
//                val output = classifyWithTFLite(tensorImage)
//
//                val outputList = output.map {
//                    /** Scores in range 0..1.0 for each of the output classes. */
//                    if (it < options.probabilityThreshold) 0f else it
//                }
//
//                val categories = labels.zip(outputList).map {
//                    Category(label = it.first, score = it.second)
//                }.sortedByDescending { it.score }.take(options.resultCount)
//
//                val inferenceTime = SystemClock.uptimeMillis() - startTime
//                if (isActive) {
//                    _classification.emit(ClassificationResult(categories, inferenceTime))
//                }
//            }
//        } catch (e: Exception) {
//            Log.i(TAG, "Image classification error occurred: ${e.message}")
//            _error.emit(e)
//        }
//    }

//    private fun classifyWithTFLite(tensorImage: TensorImage): FloatArray {
//        val outputShape = interpreter!!.getOutputTensor(0).shape()
//        val outputBuffer = FloatBuffer.allocate(outputShape[1])
//
//        outputBuffer.rewind()
//        interpreter?.run(tensorImage.tensorBuffer.buffer, outputBuffer)
//        outputBuffer.rewind()
//        val output = FloatArray(outputBuffer.capacity())
//        outputBuffer.get(output)
//        return output
//    }


//    /** Load metadata from model*/
//    private fun getModelMetadata(litertBuffer: ByteBuffer): List<String> {
//        val metadataExtractor = MetadataExtractor(litertBuffer)
//        val labels = mutableListOf<String>()
//        if (metadataExtractor.hasMetadata()) {
//            val inputStream = metadataExtractor.getAssociatedFile("labels_without_background.txt")
//            labels.addAll(readFileInputStream(inputStream))
//            Log.i(
//                TAG, "Successfully loaded model metadata ${metadataExtractor.associatedFileNames}"
//            )
//        }
//        return labels
//    }

//    /** Retrieve Map<String, Int> from metadata file */
//    private fun readFileInputStream(inputStream: InputStream): List<String> {
//        val reader = BufferedReader(InputStreamReader(inputStream))
//
//        val list = mutableListOf<String>()
//        var index = 0
//        var line = ""
//        while (reader.readLine().also { if (it != null) line = it } != null) {
//            list.add(line)
//            index++
//        }
//
//        reader.close()
//        return list
//    }

}