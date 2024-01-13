package com.smh.nxleave.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        cacheDir, /* directory */
    )
}

fun Context.sendEmail() {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "message/rfc822"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("soeminhet@gmail.com"))
    startActivity(intent)
}

fun Context.callPhone() {
    val phoneNumber = "091112233"
    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
    startActivity(intent)
}

fun Context.goToSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    startActivity(intent)
}