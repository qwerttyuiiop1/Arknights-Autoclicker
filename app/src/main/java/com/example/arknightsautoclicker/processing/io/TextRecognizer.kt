package com.example.arknightsautoclicker.processing.io

import android.graphics.Bitmap
import com.example.arknightsautoclicker.processing.exe.Promise
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * recognize text
 */
class TextRecognizer {
    val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    fun getText(
        bitmap: Bitmap,
        onSuccess: (Text) -> Unit,
        onFail: (Exception) -> Unit = {throw it},
    ) {
        recognizer.process(bitmap, 0)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFail)
    }

    suspend fun getText(bitmap: Bitmap): Text {
        val future = Promise<Text>()
        getText(bitmap, onSuccess = {
            future.complete(it)
        })
        return future.await()
    }
}