package com.simplemobiletools.gallery.pro.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bruhascended.cv.ImageCategory
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.isRPlus
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.gallery.pro.extensions.categoryDao
import com.simplemobiletools.gallery.pro.extensions.config
import com.simplemobiletools.gallery.pro.extensions.mediaDB
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.DURATION_ID
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.Duration
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.IMAGE_CATEGORY_ID
import java.io.File

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
        mContext.categoryDao.addToSizeOfCategory(category, -totalSize, -expired.size)
        expired.forEach { mContext.mediaDB.deleteMediumPath(it.path) }
        mContext.deleteFiles(expired.map { it.toFileDirItem() })

        return Result.success()
    }

    private fun Context.deleteFiles(
        files: List<FileDirItem>
    ) {
        files.forEach { file ->
            deleteFileBg(file)
        }
    }

    private fun Context.deleteFileBg(
        fileDirItem: FileDirItem,
        allowDeleteFolder: Boolean = true
    ) {
        val path = fileDirItem.path
        if (isRestrictedSAFOnlyRoot(path)) {
            deleteAndroidSAFDirectory(path, allowDeleteFolder)
        } else {
            val file = File(path)
            if (!isRPlus() && file.absolutePath.startsWith(internalStoragePath) && !file.canWrite()) {
                return
            }

            var fileDeleted = !isPathOnOTG(path) && ((!file.exists() && file.length() == 0L) || file.delete())
            if (fileDeleted) {
                deleteFromMediaStore(path) { needsRescan ->
                    if (needsRescan) {
                        rescanAndDeletePath(path) {}
                    }
                }
            } else {
                if (getIsPathDirectory(file.absolutePath) && allowDeleteFolder) {
                    fileDeleted = deleteRecursively(file)
                }

                if (!fileDeleted) {
                    if (needsStupidWritePermissions(path)) {
                        trySAFFileDelete(fileDirItem, allowDeleteFolder)
                    } else if (isAccessibleWithSAFSdk30(path)) {
                        deleteDocumentWithSAFSdk30(fileDirItem, allowDeleteFolder) {}
                    }
                }
            }
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles() ?: return file.delete()
            for (child in files) {
                deleteRecursively(child)
            }
        }

        return file.delete()
    }
}
