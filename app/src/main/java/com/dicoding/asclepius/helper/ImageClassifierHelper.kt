package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class ImageClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResults: Int = 3,
    val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null


    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder =  BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(context, modelName,optionsBuilder.build() )
        } catch(e: IllegalStateException){
            classifierListener?.onError(context.getString(R.string.init_classifier_failed))
        }

    }

    fun classifyStaticImage(imgUri: Uri) {
        if(imageClassifier == null){
            setupImageClassifier()
        }
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224,224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .build()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val source = ImageDecoder.createSource(context.contentResolver, imgUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imgUri)
        }.copy(Bitmap.Config.ARGB_8888, true)?.let {bitmap->
            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
            val results = imageClassifier?.classify(tensorImage)
            classifierListener?.onResults(results)
        }
    }


    interface ClassifierListener{
        fun onError(error: String)
        fun onResults(results: List<Classifications>?)
    }
}