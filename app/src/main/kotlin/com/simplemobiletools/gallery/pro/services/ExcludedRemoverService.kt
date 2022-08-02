package com.simplemobiletools.gallery.pro.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleService
import com.bruhascended.cv.ImageCategory
import com.bruhascended.cv.ImageModel
import com.bruhascended.cv.RunTimeAnalyzer
import com.simplemobiletools.gallery.pro.BuildConfig
import com.simplemobiletools.gallery.pro.R
import com.simplemobiletools.gallery.pro.activities.SplashActivity
import com.simplemobiletools.gallery.pro.extensions.categoryDao
import com.simplemobiletools.gallery.pro.extensions.config
import com.simplemobiletools.gallery.pro.extensions.directoryDao
import com.simplemobiletools.gallery.pro.extensions.mediaDB
import com.simplemobiletools.gallery.pro.models.Directory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.round

class ExcludedRemoverService: LifecycleService() {
    private lateinit var imageModel: ImageModel
    private lateinit var notificationManager: NotificationManager
    private var jobActive = false
    private var startId = 0

    private lateinit var pendingIntent: PendingIntent

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (jobActive) return super.onStartCommand(intent, flags, startId)
        jobActive = true

        this.startId = startId
        pendingIntent = Intent(this, SplashActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }
        imageModel = ImageModel(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val chan = NotificationChannel(
            applicationContext.packageName,
            "My Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(chan)

        startForeground(ONGOING_NOTIFICATION_ID, getNotification())

        CoroutineScope(Dispatchers.IO).launch {
            directoryDao.getAll().also {
                if (it.isNotEmpty()) {
                    processQueue(it)
                    notificationManager.cancel(ONGOING_NOTIFICATION_ID)
                    stopForeground(true)
                    stopSelf()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun processQueue(directories: List<Directory>) {
        val excludedDirectories = config.excludedFolders
        for (dir in directories) {
            for (medium in mediaDB.getMediaFromPathWithPredictions(dir.path)) {
                if (excludedDirectories.any {
                        medium.path.startsWith(it)
                }) {
                    categoryDao.addToSizeOfCategory(medium.category ?: ImageCategory.Other, -medium.size, -1)
                    mediaDB.deleteMediumPath(medium.path)
                }
            }
        }
    }

    private fun getNotification(): Notification {
        return Notification.Builder(this, applicationContext.packageName)
            .setContentTitle("Removing excluded folders.")
            .setSmallIcon(R.mipmap.ic_app_launcher)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.organising_your_gallery))
            .setProgress(0,0, true)
            .build()
    }


    companion object {
        const val ONGOING_NOTIFICATION_ID = 123412
    }
}
