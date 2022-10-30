package cz.honest.city.internal.provider.rate

import cz.city.honest.service.gateway.internal.InternalRateGateway

class ImageCameraAnalyzerProvider(private val imageExchangeRateProvider: ImageExchangeRateProvider) :
    InternalRateGateway {

    fun provide(textCallback: (lines: List<String>) -> Unit) =
        ImageCameraAnalyzer(imageExchangeRateProvider, textCallback)

}