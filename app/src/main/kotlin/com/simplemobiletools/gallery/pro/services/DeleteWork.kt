package com.simplemobiletools.gallery.pro.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bruhascended.cv.ImageCategory
import com.simplemobiletools.gallery.pro.R
import com.simplemobiletools.gallery.pro.activities.MediaCategoryActivity
import com.simplemobiletools.gallery.pro.extensions.config
import com.simplemobiletools.gallery.pro.extensions.mediaDB
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.DURATION_ID
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.Duration
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.IMAGE_CATEGORY_ID
import kotlin.math.pow

class DeleteWork(
    private val mContext: Context,
    workerParams: WorkerParameters
): Worker(mContext, workerParams) {

    override fun doWork(): Result {
        // extract scheduled message data
        val category = ImageCategory.values()[inputData.getInt(IMAGE_CATEGORY_ID, 0)]
        val duration = Duration.values()[inputData.getInt(DURATION_ID, 0)]

        val expired = mContext.mediaDB.getMediaFromCategory(category, mContext.config.imageCategoryThreshold)
            .filter { duration.isOlder(mContext, it.modified) }

        val totalSize = expired.sumOf { it.size }

        if (expired.size > 1) {

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            val chan = NotificationChannel(
                applicationContext.packageName,
                "My Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(chan)

            notificationManager.notify(1234, getNotification(expired.size, totalSize, category, duration))
        }
        return Result.success()
    }

    private fun getNotification(count: Int, size: Long, category: ImageCategory, duration: Duration): Notification {
        return Notification.Builder(applicationContext, applicationContext.packageName)
            .setContentTitle("$count Images of ${category.fullName} are taking up ${size.toUserReadableSize()} on your phone, tap to delete.")
            .setSmallIcon(R.mipmap.ic_app_launcher)
            .setContentIntent(
                Intent(applicationContext, MediaCategoryActivity::class.java).let { notificationIntent ->
                    notificationIntent.putExtra("CATEGORY", category.ordinal)
                    notificationIntent.putExtra("DURATION", duration.ordinal)
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            )
            .setAutoCancel(true)
            .build()
    }


    private fun Long.toUserReadableSize(): String {
        val len = toString().length - 1
        val sizes = arrayOf("B", "KB", "MB", "GB")
        val yeah = (this/10.0.pow((len - (len % 3)).toDouble()))
        return when(len % 3) {
            0 -> "%.2f".format(yeah)
            1 -> "%.1f".format(yeah)
            else -> yeah.toInt().toString()
        } + " ${sizes[len/3]}"
    }
}
