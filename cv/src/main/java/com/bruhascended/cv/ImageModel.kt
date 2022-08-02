package com.bruhascended.cv

import android.content.Context
import android.graphics.Bitmap
import com.bruhascended.cv.ml.CategoryWithMetadata
import com.bruhascended.cv.ml.SsdMobilenetV1
import com.bruhascended.cv.ops.LimitPixelsOp
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod.BILINEAR
import org.tensorflow.lite.support.model.Model

class ImageModel (
    context: Context,
    private val extractObjects: Boolean = false,
) {

    private val inputDim = 224

    private var objectModel: SsdMobilenetV1
    private var categoryModel: CategoryWithMetadata

    private var categoryProcessor: ImageProcessor
    private var objectProcessor: ImageProcessor

    init {
        val options = Model.Options.Builder()
            .setNumThreads(4)
            .setDevice(Model.Device.NNAPI)
            .build()

        objectProcessor = ImageProcessor.Builder()
            .add(LimitPixelsOp(512*512))
            .build()

        categoryProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputDim, inputDim, BILINEAR))
            .build()

        categoryModel = CategoryWithMetadata.newInstance(context, options)
        objectModel = SsdMobilenetV1.newInstance(context, options)
    }

    fun fetchResults(bitmap: Bitmap): Predictions {
        var tensor = objectProcessor.process(TensorImage(DataType.FLOAT32).apply { load(bitmap) })
        val objectConfidences = if (extractObjects) objectModel
            .process(tensor)
            .detectionResultList
            .filter { it.scoreAsFloat >= 0.5 }
            .map { it.categoryAsString to it.scoreAsFloat }
        else emptyList()

        tensor = categoryProcessor.process(TensorImage(DataType.FLOAT32).apply { load(bitmap) })
        val categoryConfidences = categoryModel
                .process(tensor)
                .probabilityAsCategoryList
                .map { it.score }
                .toTypedArray()

        return Predictions(
            categoryConfidences,
            objectConfidences
        )
    }

    // Releases model resources if no longer used.
    fun close() {
        objectModel.close()
        categoryModel.close()
    }
}
