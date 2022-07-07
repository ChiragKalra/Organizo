package com.simplemobiletools.gallery.pro.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bruhascended.cv.ImageCategory
import com.bumptech.glide.signature.ObjectKey
import com.simplemobiletools.commons.extensions.formatSize
import com.simplemobiletools.commons.helpers.SORT_BY_NAME
import com.simplemobiletools.commons.helpers.SORT_BY_SIZE
import com.simplemobiletools.gallery.pro.helpers.LOCATION_INTERNAL
import com.simplemobiletools.gallery.pro.helpers.TYPE_IMAGES
import com.simplemobiletools.gallery.pro.helpers.TYPE_USELESS

@Entity(tableName = "categories", indices = [Index(value = ["image_category"], unique = true)])
data class CategoryEntry(
    @PrimaryKey @ColumnInfo(name = "image_category") var imageCategory: ImageCategory,
    @ColumnInfo(name = "media_count") var mediaCnt: Int,
    @ColumnInfo(name = "size") var size: Long,
) {

    constructor() : this(ImageCategory.Other, 0, 0L)

    fun getBubbleText(sorting: Int) = when {
        sorting and SORT_BY_NAME != 0 -> imageCategory.name
        sorting and SORT_BY_SIZE != 0 -> size.formatSize()
        else -> imageCategory.name
    }

    fun getKey() = ObjectKey(imageCategory.name)

    fun toDirectory(): Directory {
        val it = this
        return Directory().apply {
            id = it.imageCategory.ordinal.toLong()
            path = it.imageCategory.description
            tmb = it.imageCategory.thumbnail
            name = it.imageCategory.fullName
            mediaCnt = it.mediaCnt
            size = it.size
            types = TYPE_IMAGES or TYPE_USELESS
            location = LOCATION_INTERNAL
            sortValue = it.size.toString()
        }
    }
}
