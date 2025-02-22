package com.simplemobiletools.gallery.pro.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.bruhascended.cv.ImageCategory
import com.bruhascended.cv.Predictions
import com.simplemobiletools.gallery.pro.models.Medium

@Dao
interface MediumDao {
    @Query("""
        SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id
        FROM media 
        WHERE deleted_ts = 0 AND parent_path = :path COLLATE NOCASE
        ORDER BY size DESC
    """)
    fun getMediaFromPath(path: String): List<Medium>

    @Query("""
        SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id,
        category, category_confidence, processing_start, predictions
        FROM media 
        WHERE full_path = :fullPath COLLATE NOCASE
    """)
    fun getMedium(fullPath: String): Medium?

    @Query("""
        SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id
        FROM media 
        WHERE deleted_ts = 0 AND category = :category AND category_confidence >= :threshold
        ORDER BY size DESC
    """)
    fun getMediaFromCategory(category: ImageCategory, threshold: Float): List<Medium>

    @Query("""
        SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id,
        category, category_confidence, processing_start, predictions
        FROM media 
        WHERE deleted_ts = 0 AND parent_path = :path COLLATE NOCASE
        ORDER BY size DESC
    """)
    fun getMediaFromPathWithPredictions(path: String): List<Medium>

    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts = 0 AND is_favorite = 1")
    fun getFavorites(): List<Medium>

    @Query("SELECT COUNT(filename) FROM media WHERE deleted_ts = 0 AND is_favorite = 1")
    fun getFavoritesCount(): Long

    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts != 0")
    fun getDeletedMedia(): List<Medium>

    @Query("SELECT COUNT(filename) FROM media WHERE deleted_ts != 0")
    fun getDeletedMediaCount(): Long

    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts < :timestmap AND deleted_ts != 0")
    fun getOldRecycleBinItems(timestmap: Long): List<Medium>

    @Insert(onConflict = ABORT)
    fun insert(medium: Medium)

    @Insert(onConflict = ABORT)
    fun insertAll(media: List<Medium>)

    @Delete
    fun deleteMedia(vararg medium: Medium)

    @Query("DELETE FROM media WHERE full_path = :path COLLATE NOCASE")
    fun deleteMediumPath(path: String)

    @Query("UPDATE OR REPLACE media SET predictions = :predictions, category = :category, category_confidence = :confidence WHERE full_path = :path COLLATE NOCASE")
    fun updateMediumPredictions(path: String, predictions: Predictions, category: ImageCategory, confidence: Float)

    @Query("UPDATE OR REPLACE media SET processing_start = :time WHERE full_path = :path COLLATE NOCASE")
    fun updateMediumProcessStart(path: String, time: Long)

    @Query("UPDATE OR REPLACE media SET filename = :newFilename, full_path = :newFullPath, parent_path = :newParentPath WHERE full_path = :oldPath COLLATE NOCASE")
    fun updateMedium(newFilename: String, newFullPath: String, newParentPath: String, oldPath: String)

    @Query("UPDATE OR REPLACE media SET full_path = :newPath, deleted_ts = :deletedTS WHERE full_path = :oldPath COLLATE NOCASE")
    fun updateDeleted(newPath: String, deletedTS: Long, oldPath: String)

    @Query("UPDATE media SET date_taken = :dateTaken WHERE full_path = :path COLLATE NOCASE")
    fun updateFavoriteDateTaken(path: String, dateTaken: Long)

    @Query("UPDATE media SET is_favorite = :isFavorite WHERE full_path = :path COLLATE NOCASE")
    fun updateFavorite(path: String, isFavorite: Boolean)

    @Query("UPDATE media SET is_favorite = 0")
    fun clearFavorites()

    @Query("DELETE FROM media WHERE deleted_ts != 0")
    fun clearRecycleBin()
}
