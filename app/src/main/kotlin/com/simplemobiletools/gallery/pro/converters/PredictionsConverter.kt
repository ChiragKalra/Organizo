package com.simplemobiletools.gallery.pro.converters

import androidx.room.TypeConverter
import com.bruhascended.cv.ImageCategory
import com.bruhascended.cv.Predictions

class PredictionsConverter {

    @TypeConverter
    fun listToJson(predictions: Predictions?): String {
        return if (predictions == null) ""
        else StringBuilder().apply {
            for (v in predictions.categories) append("${v},")
            for ((name, p) in predictions.objects) append("$name:$p,")
        }.toString()
    }

    @TypeConverter
    fun jsonToList(value: String?): Predictions? {
        return if (value.isNullOrEmpty()) null
        else {
            val out = value.split(",")
            val categories = out.subList(0, Predictions.categoryCount)
                .map { it.toFloat() }
                .toTypedArray()
            val objects = out.subList(Predictions.categoryCount, out.size)
                .filter { ':' in it && it.length > 2}
                .map {
                    val (name, p) = it.split(':')
                    name to p.toFloat()
                }
            Predictions(categories, objects)
        }
    }

    @TypeConverter
    fun imageCategoryToString(category: ImageCategory) = category.name

    @TypeConverter
    fun stringToImageCategory(name: String) = ImageCategory.valueOf(name)

}
