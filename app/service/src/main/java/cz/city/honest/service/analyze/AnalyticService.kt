package cz.city.honest.service.analyze

import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.service.gateway.internal.InternalImageNameAnalyticGateway
import cz.city.honest.service.gateway.internal.InternalImageRateAnalyticGateway

class AnalyticService(
    private val internalImageRateAnalyticGateway: InternalImageRateAnalyticGateway,
    private val internalImageNameAnalyticGateway: InternalImageNameAnalyticGateway
) {

    fun analyzeRate(imageData: AnalyzeImageData, textCallback: (lines: List<String>) -> Unit) =
        internalImageRateAnalyticGateway.analyze(imageData, textCallback)

    fun getRateResult() = internalImageRateAnalyticGateway.getResult()

    fun analyzeName(imageData: AnalyzeImageData, textCallback: (lines: List<String>) -> Unit) =
        internalImageNameAnalyticGateway.analyze(imageData, textCallback)

    fun getNameResult() = internalImageNameAnalyticGateway.getResult()
}