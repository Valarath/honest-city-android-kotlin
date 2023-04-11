package cz.honest.city.internal.analyze

import com.google.mlkit.vision.common.InputImage
import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.service.gateway.internal.InternalImageNameAnalyticGateway
import cz.honest.city.internal.provider.ImageSubjectNameProvider
import cz.honest.city.internal.provider.rate.ImageUtils
import io.reactivex.rxjava3.core.Observable

class SubjectNameAnalyticGateway (private val imageSubjectNameProvider: ImageSubjectNameProvider) :
    InternalImageNameAnalyticGateway {

    override fun analyze(
        imageData: AnalyzeImageData,
        textCallback: (lines: List<String>) -> Unit
    ): Observable<Unit> =
        Observable.just(imageData)
            .map { InputImage.fromBitmap(ImageUtils.getInputImage(imageData), 0) }
            .map { imageSubjectNameProvider.provide(it, textCallback) }

    override fun getResult() = imageSubjectNameProvider.getImageResult()
}