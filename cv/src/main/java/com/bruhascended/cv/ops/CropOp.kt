package com.bruhascended.cv.ops

import android.graphics.Bitmap
import android.graphics.PointF
import org.tensorflow.lite.support.image.ImageOperator
import org.tensorflow.lite.support.image.TensorImage


class CropOp(private val targetAspectRatio: Float = 1f) : ImageOperator {

    override fun apply(image: TensorImage): TensorImage {
        val bm = image.bitmap
        val w = bm.width
        val h = bm.height
        image.load(Bitmap.createBitmap(bm, 0, 0, getOutputImageWidth(h, w), getOutputImageHeight(h, w)))
        return image
    }

    override fun getOutputImageHeight(h: Int, w: Int) =
        if (h*targetAspectRatio <= w) h else (w/targetAspectRatio).toInt()

    override fun getOutputImageWidth(h: Int, w: Int) =
        if (h*targetAspectRatio <= w) (h*targetAspectRatio).toInt() else w


    override fun inverseTransform(point: PointF, inputImageHeight: Int, inputImageWidth: Int): PointF {
        return PointF(
            point.x * inputImageWidth.toFloat() / getOutputImageWidth(inputImageHeight, inputImageWidth),
            point.y * inputImageHeight.toFloat() / getOutputImageHeight(inputImageHeight, inputImageWidth)
        )
    }
}
