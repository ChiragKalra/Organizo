package com.simplemobiletools.gallery.pro.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleService
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

class FeatureExtractorService: LifecycleService() {
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
            directoryDao.getAllFlow().collect {
                if (it.isNotEmpty()) {
                    processQueue(it) { eta, done, total ->
                        notificationManager.notify(ONGOING_NOTIFICATION_ID, getNotification(eta, done, total))
                    }
                    notificationManager.cancel(ONGOING_NOTIFICATION_ID)
                    stopForeground(true)
                    stopSelf()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun processQueue(directories: List<Directory>, progressCallback: (eta: Long, done: Int, total: Int) -> Unit) {
        var totalCount = 0
        var doneCount = 0
        var allCount = 0
        for (dir in directories) {
            for (medium in mediaDB.getMediaFromPathWithPredictions(dir.path)) {
                if (medium.isQueuedForProcessing) {
                    totalCount++
                }
                allCount++
            }
        }
        val runTimeAnalyzer = RunTimeAnalyzer(BuildConfig.DEBUG)
        for (dir in directories) {
            for (medium in mediaDB.getMediaFromPathWithPredictions(dir.path)) {
                if (medium.isQueuedForProcessing) {
                    mediaDB.updateMediumProcessStart(medium.path, Calendar.getInstance().timeInMillis)
                    val bm = BitmapFactory.decodeFile(medium.path) ?: continue
                    val preds = imageModel.fetchResults(bm)
                    val (category, conf) = preds.getCategory()
                    mediaDB.updateMediumPredictions(medium.path, preds, category, conf)
                    if (conf >= config.imageCategoryThreshold) {
                        categoryDao.addToSizeOfCategory(category, medium.size, 1)
                    }
                    runTimeAnalyzer.log()
                    val eta = ((totalCount - doneCount) * (runTimeAnalyzer.movingAverage ?: .0)).toLong()
                    progressCallback(eta, doneCount, totalCount)
                    doneCount++
                }
            }
        }
    }

    private fun getNotification(eta: Long = 0, done: Int = 0, total: Int = 0): Notification {
        val sec = (eta / 1000) % 60
        val min = round((eta / 1000) / 60.0).toInt()
        val etaStr = if (total > 0) {
            when {
                sec > 90 -> getString(R.string.x_mins_remaining, min)
                sec > 15 -> getString(R.string.less_than_min_remaining)
                sec > 3 -> getString(R.string.just_few_secs)
                else -> getString(R.string.finishing_up)
            }
        } else ""
        return Notification.Builder(this, applicationContext.packageName)
            .setContentTitle(getString(R.string.organising_your_gallery) + etaStr)
            .setSmallIcon(R.mipmap.ic_app_launcher)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.organising_your_gallery))
            .setProgress(total, done, total == 0)
            .build()
    }


    companion object {
        const val ONGOING_NOTIFICATION_ID = 123412
    }
}
