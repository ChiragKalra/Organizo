package com.bruhascended.cv.ops

import android.graphics.Bitmap
import android.graphics.PointF
import org.tensorflow.lite.support.image.ImageOperator
import org.tensorflow.lite.support.image.TensorImage
import kotlin.math.sqrt


class LimitPixelsOp(private val maxPixels: Int) : ImageOperator {

    private val sqrtPixels = sqrt(maxPixels.toFloat())

    override fun apply(image: TensorImage): TensorImage {
        val bm = image.bitmap
        val w = bm.width
        val h = bm.height
        image.load(Bitmap.createScaledBitmap(bm, getOutputImageWidth(h, w), getOutputImageHeight(h, w), true))
        return image
    }

    override fun getOutputImageHeight(h: Int, w: Int): Int {
        return if (h*w <= maxPixels) h
        else ((h * sqrtPixels) / w).toInt()
    }

    override fun getOutputImageWidth(h: Int, w: Int): Int {
        return if (h*w <= maxPixels) w
        else ((w * sqrtPixels) / h).toInt()
    }

    override fun inverseTransform(point: PointF, inputImageHeight: Int, inputImageWidth: Int): PointF {
        return PointF(
            point.x * inputImageWidth.toFloat() / getOutputImageWidth(inputImageHeight, inputImageWidth),
            point.y * inputImageHeight.toFloat() / getOutputImageHeight(inputImageHeight, inputImageWidth)
        )
    }
}
