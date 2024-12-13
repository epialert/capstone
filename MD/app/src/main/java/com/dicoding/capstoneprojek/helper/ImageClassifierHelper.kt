package com.dicoding.capstoneprojek.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.dicoding.capstoneprojek.ml.Modelkasaran
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener
) {
    private var model: Modelkasaran? = null

    // Daftar nama kelas penyakit sesuai dengan output model
    private val classLabels = listOf(
        "Squamous Cell Carcinoma", "Pigmented Benign Keratosis", "Basal Cell Carcinoma",
        "Melanoma", "Seborrheic Keratosis", "Nevus",
        "Actinic Keratosis"
    )

    // Tambahkan daftar kode label dalam format huruf kecil dengan underscore
    private val labelCodes = listOf(
        "squamous_cell_carcinoma", "pigmented_benign_keratosis", "basal_cell_carcinoma",
        "melanoma", "seborrheic_keratosis", "nevus",
        "actinic_keratosis"
    )

    init {
        try {
            // Inisialisasi model saat helper dibuat
            model = Modelkasaran.newInstance(context)
        } catch (e: Exception) {
            classifierListener.onError("Error initializing model: ${e.message}")
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        try {
            // Decode image to Bitmap
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }

            if (bitmap == null) {
                throw IllegalArgumentException("Failed to decode image from URI")
            }

            // Convert the Bitmap to ARGB_8888 format if it's not already
            val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            // Resize bitmap to 224x224 for model input
            val scaledBitmap = Bitmap.createScaledBitmap(argbBitmap, 224, 224, true)

            // Convert Bitmap to TensorImage (using TensorFlow Lite helper class)
            val tensorImage = TensorImage.fromBitmap(scaledBitmap)

            // Create a TensorBuffer for inference input
            val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), org.tensorflow.lite.DataType.FLOAT32)

            // Get the byte array from TensorImage
            val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            // Normalize the image to [0, 1] range and convert to byte buffer
            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val pixel = scaledBitmap.getPixel(x, y)
                    byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f) // Red
                    byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)  // Green
                    byteBuffer.putFloat((pixel and 0xFF) / 255.0f)       // Blue
                }
            }

            inputBuffer.loadBuffer(byteBuffer)

            // Run inference
            model?.let {
                val outputs = it.process(inputBuffer)
                val outputFeature = outputs.outputFeature0AsTensorBuffer

                // Convert output to list of ClassificationResult
                val results = outputFeature.floatArray.mapIndexed { index, score ->
                    // Gantilah label numerik dengan nama kelas yang sesuai
                    val label = classLabels.getOrNull(index) ?: "Unknown"
                    val labelCode = labelCodes.getOrNull(index) ?: "unknown"

                    // Kembalikan objek yang tetap memuat labelCode, tetapi tidak dikirimkan ke listener
                    ClassificationResult(label = label, score = score, labelCode = labelCode)
                }

                // Urutkan hasil klasifikasi berdasarkan skor tertinggi
                val sortedResults = results.sortedByDescending { it.score }

                // Ambil hasil dengan skor tertinggi
                val highestResult = sortedResults.firstOrNull()

                // Pass results back to listener
                highestResult?.let {
                    // Kirimkan hanya label dan score, tanpa labelCode
                    val result = mapOf(
                        "predicted_class" to it.label,  // Hanya tampilkan label
                        "confidence" to it.score  // Hanya tampilkan skor
                    )
                    classifierListener.onResults(result)  // Mengirim hasil ke listener
                } ?: run {
                    classifierListener.onError("No valid result")
                }
            }
        } catch (e: Exception) {
            classifierListener.onError("Error during classification: ${e.message}")
        }
    }

    fun close() {
        model?.close()
    }

    interface ClassifierListener {
        fun onResults(result: Map<String, Any>)  // Parameter berubah menjadi Map
        fun onError(error: String)
    }
}
