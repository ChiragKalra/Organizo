package com.simplemobiletools.gallery.pro.helpers

import android.content.Context
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.isRPlus
import com.simplemobiletools.commons.models.FileDirItem
import java.io.File

class DeleteHelper {

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
