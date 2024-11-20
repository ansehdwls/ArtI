package com.hexa.arti.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PictureUtil"

private fun uriToFile(context: Context, uri: Uri): File {
    val contentResolver = context.contentResolver
    val file =
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image${System.currentTimeMillis()}.jpg")

    contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
    }
    return file
}

private fun compressImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    val compressedFile = File(file.parent, "compressed_${file.name}")
    FileOutputStream(compressedFile).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // 80% 압축 품질
    }
    return compressedFile
}

fun handleImage(imageUri: Uri, context: Context, name :String) : MultipartBody.Part {
    var file = uriToFile(context, imageUri)

    val maxSize = 10 * 1024 * 1024 // 10MB
    if (file.length() > maxSize) {
        file = compressImage(file)

        if (file.length() > maxSize) {


            return  MultipartBody.Part.createFormData(
                name,
                ""
            )
        }
    }

    if(isJpgFile(file)) {
        Log.d(TAG, "handleImage: ${file.name}")
        Log.d(TAG, "handleImage: ${name}")
    }
    val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        name,
        file.name,
        requestFile
    )
    
}

private fun isJpgFile(file: File): Boolean {
    val fileName = file.name.lowercase()
    return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
}