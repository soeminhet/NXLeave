package com.smh.nxleave.utility

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun Uri.toFile(context: Context): File {
    val fileName = context.contentResolver.getFileName(this)
    return context.contentResolver
        .openInputStream(this)
        .use { inputStream ->
            return@use File(context.cacheDir, fileName)
                .also { it.createNewFile() }
                .apply { outputStream().use { inputStream?.copyTo(it) } }
        }
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}