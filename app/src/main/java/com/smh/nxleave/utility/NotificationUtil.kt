package com.smh.nxleave.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.smh.nxleave.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationUtil {
    const val CHANNEL_ID = "2024"
    const val CHANNEL_NAME = "Notifications"

    fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

    fun getNotificationManager(context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getNotificationManager(context).createNotificationChannel(channel)
    }

    fun getNotificationBuilder(
        context: Context,
        title: String,
        body: String
    ): NotificationCompat.Builder {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title).setContentText(body)
            .setColor(ContextCompat.getColor(context, R.color.primaryColor)).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX).setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    fun pushLocalNotification(
        context: Context,
        file: File
    ) {
        createNotificationChannel(context)

        val intent = Intent(Intent.ACTION_VIEW)
        val data = FileProvider.getUriForFile(
            context,
            context.applicationContext
                .packageName + ".provider",
            file
        )
        intent.data = data
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = getNotificationBuilder(context, "NXLeaveReport", file.name)
        builder.setContentIntent(pendingIntent)

        getNotificationManager(context).notify(createID(), builder.build())
    }
}