package cz.honest.city.internal.provider

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.mlkit.vision.common.InputImage
import cz.city.honest.analyzer.subject.SubjectAnalyzer
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import java.io.ByteArrayOutputStream


class ImageSubjectNameProvider(
    private val subjectNameAnalyzers: List<SubjectAnalyzer>,
) : ImageProvider<String>() {

    override fun mapToResult(textLines: List<String>, image: InputImage) {
        Maybe.mergeArray(*subjectNameAnalyzers.map { it.analyze(textLines) }
            .toTypedArray())
            .subscribe {
                image.bitmapInternal?.let {
                    val resultImageAsString = getImageAsBase64String(it)
                    result = Observable.just(resultImageAsString)
                }
            }
    }

    private fun getImageAsBase64String(image: Bitmap): String = ByteArrayOutputStream()
        .also { scaleImage(image).compress(Bitmap.CompressFormat.JPEG, 100, it) }
        .let { Base64.encodeToString(it.toByteArray(), Base64.DEFAULT) }

    private fun scaleImage(image:Bitmap)  =
        (image.height * (400.0 / image.width))
            .toInt()
            .let { Bitmap.createScaledBitmap(image, 400, it, true) }

}