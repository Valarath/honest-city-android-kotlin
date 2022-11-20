package cz.honest.city.internal.analyze

import com.google.mlkit.vision.common.InputImage
import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.service.gateway.internal.InternalImageAnalyticGateway
import cz.honest.city.internal.provider.rate.ImageExchangeRateProvider
import cz.honest.city.internal.provider.rate.ImageUtils
import io.reactivex.rxjava3.core.Observable

class ExchangeRateAnalyticGateway(private val imageExchangeRateProvider: ImageExchangeRateProvider) :
    InternalImageAnalyticGateway {

    override fun analyze(
        imageData: AnalyzeImageData,
        textCallback: (lines: List<String>) -> Unit
    ): Observable<Unit> =
        Observable.just(imageData)
            .map { InputImage.fromBitmap(ImageUtils.getInputImage(imageData), 0) }
            .map { imageExchangeRateProvider.provide(it, textCallback) }

    override fun getResult() = imageExchangeRateProvider.getResult()
}