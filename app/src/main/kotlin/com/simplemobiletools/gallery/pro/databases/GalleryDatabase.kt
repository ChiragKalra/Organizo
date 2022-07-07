package com.simplemobiletools.gallery.pro.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.simplemobiletools.gallery.pro.converters.PredictionsConverter
import com.simplemobiletools.gallery.pro.interfaces.*
import com.simplemobiletools.gallery.pro.models.*

@Database(entities = [CategoryEntry::class, Directory::class, Medium::class, Widget::class, DateTaken::class, Favorite::class], version = 10)
@TypeConverters(PredictionsConverter::class)
abstract class GalleryDatabase : RoomDatabase() {

    abstract fun CategoryDao(): CategoryDao

    abstract fun DirectoryDao(): DirectoryDao

    abstract fun MediumDao(): MediumDao

    abstract fun WidgetsDao(): WidgetsDao

    abstract fun DateTakensDao(): DateTakensDao

    abstract fun FavoritesDao(): FavoritesDao

    companion object {
        private var db: GalleryDatabase? = null

        fun getInstance(context: Context): GalleryDatabase {
            if (db == null) {
                synchronized(GalleryDatabase::class) {
                    if (db == null) {
                        db = Room.databaseBuilder(context.applicationContext, GalleryDatabase::class.java, "gallery.db")
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return db!!
        }

        fun destroyInstance() {
            if (db?.isOpen == true) {
                db?.close()
            }
            db = null
        }

    }
}
