package com.simplemobiletools.gallery.pro.services

import android.content.Context
import androidx.work.*
import com.bruhascended.cv.ImageCategory
import java.util.*
import java.util.concurrent.TimeUnit

class CleanupManager(mContext: Context) {
    private val mWorkManager = WorkManager.getInstance(mContext)

    fun schedule(imageCategory: ImageCategory, duration: Duration) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val request = PeriodicWorkRequestBuilder<DeleteWork>(1, TimeUnit.DAYS)
            .addTag(imageCategory.name)
            .setConstraints(constraints)
            .setInputData(
                Data.Builder()
                    .putInt(IMAGE_CATEGORY_ID, imageCategory.ordinal)
                    .putInt(DURATION_ID, duration.ordinal)
                    .build()
            ).build()

        mWorkManager.enqueueUniquePeriodicWork(
            imageCategory.name,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    companion object {
        const val IMAGE_CATEGORY_ID = "IMAGE_CATEGORY_ID"
        const val DURATION_ID = "DURATION_ID"

        enum class Duration (val timeInMs: Long, val fullName: String) {
            Day(24 * 60 * 60 * 1000L, "One Day"),
            Week(7 * 24 * 60 * 60 * 1000L, "One Week"),
            Month(30 * 24 * 60 * 60 * 1000L, "One Month"),
            Quarter(3 * 30 * 24 * 60 * 60 * 1000L, "Three Months"),
            HalfYear(6 * 30 * 24 * 60 * 60 * 1000L, "Six Months"),
            Year(365 * 24 * 60 * 60 * 1000L, "One Year");

            fun isWithin(context: Context, timeInMs: Long) = getTimeRn(context) - timeInMs <= this.timeInMs

            fun isOlder(context: Context,timeInMs: Long) = getTimeRn(context)- timeInMs > this.timeInMs

            private fun getTimeRn(context: Context) =
                Calendar.getInstance(context.resources.configuration.locales[0]).timeInMillis
        }
    }
}
