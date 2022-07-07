package com.simplemobiletools.gallery.pro.activities

import android.content.Intent
import com.bruhascended.cv.ImageCategory
import com.simplemobiletools.commons.activities.BaseSplashActivity
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.gallery.pro.extensions.*
import com.simplemobiletools.gallery.pro.models.CategoryEntry
import com.simplemobiletools.gallery.pro.models.Favorite
import com.simplemobiletools.gallery.pro.services.FeatureExtractorService

class SplashActivity : BaseSplashActivity() {
    override fun initActivity() {
        // check if previously selected favorite items have been properly migrated into the new Favorites table
        if (config.wereFavoritesMigrated) {
            launchActivity()
        } else {
            if (config.appRunCount == 0) {
                populateCategories()
                config.wereFavoritesMigrated = true
                launchActivity()
            } else {
                config.wereFavoritesMigrated = true
                ensureBackgroundThread {
                    val favorites = ArrayList<Favorite>()
                    val favoritePaths = mediaDB.getFavorites().map { it.path }.toMutableList() as ArrayList<String>
                    favoritePaths.forEach {
                        favorites.add(getFavoriteFromPath(it))
                    }
                    favoritesDB.insertAll(favorites)

                    runOnUiThread {
                        launchActivity()
                    }
                }
            }
        }
    }

    private fun launchActivity() {
        startActivity(Intent(this, CategoryActivity::class.java))
        finish()
    }

    private fun populateCategories() {
        ensureBackgroundThread {
            categoryDao.insertAll(ImageCategory.values().map { CategoryEntry(it, 0, 0L) })
        }
    }
}
