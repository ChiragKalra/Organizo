package com.simplemobiletools.gallery.pro.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.bruhascended.cv.ImageCategory
import com.simplemobiletools.gallery.pro.models.CategoryEntry

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): List<CategoryEntry>

    @Query("SELECT * FROM categories WHERE image_category != :category AND media_count > 0 " +
        "ORDER BY size DESC")
    fun getAllExceptOther(category: ImageCategory = ImageCategory.Other): List<CategoryEntry>

    @Insert(onConflict = REPLACE)
    fun insert(categories: CategoryEntry)

    @Insert(onConflict = REPLACE)
    fun insertAll(categories: List<CategoryEntry>)

    @Query("UPDATE categories SET size = size + :add, media_count = media_count + :count WHERE image_category == :category")
    fun addToSizeOfCategory(category: ImageCategory, add: Long, count: Int = 1)
}
