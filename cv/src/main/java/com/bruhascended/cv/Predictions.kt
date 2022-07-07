package com.bruhascended.cv

data class Predictions(
    val categories: Array<Float>,
    val objects: List<Pair<String, Float>>,
) {

    val features: Array<Float>
    get() = categories

    fun getCategory(): Pair<ImageCategory, Float> {
        val max = categories.maxOfOrNull { it } ?: return ImageCategory.Other to 1f
        if (max < 0.5) return ImageCategory.Other to 1f
        return ImageCategory.values()[categories.indexOfFirst { it == max }] to max
    }

    fun getObjects(threshold: Float = 0.95f): List<String> {
        return objects.mapNotNull { (name, it) -> if (it >= threshold) name else null }
    }

    companion object {
        const val categoryCount = 9
    }
}
