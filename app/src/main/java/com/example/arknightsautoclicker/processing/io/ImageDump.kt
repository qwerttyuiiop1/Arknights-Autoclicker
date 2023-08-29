package com.example.arknightsautoclicker.processing.io

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.Date
import java.util.Locale

/**
 * dump images to the files
 */
class ImageDump(
    private val context: Context
) {
    fun dump(bitmap: Bitmap) {
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
        getOutputStream(context).use {
            it.write(output.toByteArray())
        }
    }

    private fun getOutputStream(context: Context): OutputStream {
        val format = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val fname = "screenshot-${format.format(Date())}.jpg"
        val mimeType = "image/jpeg"
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fname)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/screenshots"
            )
        }

        val imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
        return contentResolver.openOutputStream(imageUri)!!
    }
}