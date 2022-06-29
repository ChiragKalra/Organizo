package com.simplemobiletools.gallery.pro.models

import android.content.Context
import androidx.room.*
import com.bumptech.glide.signature.ObjectKey
import com.simplemobiletools.commons.extensions.formatDate
import com.simplemobiletools.commons.extensions.formatSize
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.gallery.pro.helpers.RECYCLE_BIN

@Entity(tableName = "categories", indices = [Index(value = ["id"], unique = true)])
data class Category(
        @PrimaryKey() var id: Long,
        @ColumnInfo(name = "thumbnail") var tmb: String,
        @ColumnInfo(name = "filename") var name: String,
        @ColumnInfo(name = "media_count") var mediaCnt: Int,
        @ColumnInfo(name = "last_modified") var modified: Long,
        @ColumnInfo(name = "size") var size: Long,
        @ColumnInfo(name = "sort_value") var sortValue: String,
) {

    constructor() : this(-1, "", "", 0, 0L, 0L, "")

    fun getBubbleText(sorting: Int, context: Context, dateFormat: String? = null, timeFormat: String? = null) = when {
        sorting and SORT_BY_NAME != 0 -> name
        sorting and SORT_BY_SIZE != 0 -> size.formatSize()
        sorting and SORT_BY_DATE_MODIFIED != 0 -> modified.formatDate(context, dateFormat, timeFormat)
        else -> name
    }

    fun getKey() = ObjectKey("$id-$modified")
}
