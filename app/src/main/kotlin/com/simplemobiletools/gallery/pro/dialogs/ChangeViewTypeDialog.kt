package com.simplemobiletools.gallery.pro.dialogs

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.helpers.VIEW_TYPE_GRID
import com.simplemobiletools.commons.helpers.VIEW_TYPE_LIST
import com.simplemobiletools.gallery.pro.R
import com.simplemobiletools.gallery.pro.extensions.config
import com.simplemobiletools.gallery.pro.helpers.SHOW_ALL
import kotlinx.android.synthetic.main.dialog_change_view_type.view.*

class ChangeViewTypeDialog(val activity: BaseSimpleActivity, val fromFoldersView: Boolean, val path: String = "", val callback: () -> Unit) {
    private var view: View
    private var config = activity.config
    private var pathToUse = if (path.isEmpty()) SHOW_ALL else path

    init {
        view = activity.layoutInflater.inflate(R.layout.dialog_change_view_type, null).apply {
            val viewToCheck = if (fromFoldersView) {
                if (config.viewTypeFolders == VIEW_TYPE_GRID) {
                    change_view_type_dialog_radio_grid.id
                } else {
                    change_view_type_dialog_radio_list.id
                }
            } else {
                val currViewType = config.getFolderViewType(pathToUse)
                if (currViewType == VIEW_TYPE_GRID) {
                    change_view_type_dialog_radio_grid.id
                } else {
                    change_view_type_dialog_radio_list.id
                }
            }

            change_view_type_dialog_radio.check(viewToCheck)
        }

        AlertDialog.Builder(activity)
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                activity.setupDialogStuff(view, this)
            }
    }

    private fun dialogConfirmed() {
        val viewType = if (view.change_view_type_dialog_radio.checkedRadioButtonId == view.change_view_type_dialog_radio_grid.id) {
            VIEW_TYPE_GRID
        } else {
            VIEW_TYPE_LIST
        }

        if (fromFoldersView) {
            config.viewTypeFolders = viewType
        } else {
            config.removeFolderViewType(pathToUse)
            config.viewTypeFiles = viewType

        }


        callback()
    }
}
