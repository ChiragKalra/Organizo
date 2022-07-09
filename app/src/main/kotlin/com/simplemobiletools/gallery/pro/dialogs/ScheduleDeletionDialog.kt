package com.simplemobiletools.gallery.pro.dialogs

import androidx.appcompat.app.AlertDialog
import com.bruhascended.cv.ImageCategory
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.gallery.pro.R
import com.simplemobiletools.gallery.pro.services.CleanupManager
import com.simplemobiletools.gallery.pro.services.CleanupManager.Companion.Duration

class ScheduleDeletionDialog(
    private val activity: BaseSimpleActivity, private val imageCategory: ImageCategory
) {
    private val manager = CleanupManager(activity)
    fun show() {
        var selection = manager.getDuration(imageCategory).ordinal
        activity.toast("Note: All files permission must be granted for this feature to work.")
        AlertDialog.Builder(activity)
            .setTitle("Schedule automatic deletion for images older than:")
            .setSingleChoiceItems(Duration.values().map { it.fullName }.toTypedArray(), selection) { _, select ->
                selection = select
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                manager.schedule(imageCategory, Duration.values()[selection])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                manager.cancel(imageCategory)
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
