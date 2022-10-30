package cz.city.honest.service.analyze

import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.service.gateway.internal.InternalImageAnalyticGateway

class AnalyticService(private val internalImageAnalyticGateway: InternalImageAnalyticGateway) {

    fun analyze(imageData: AnalyzeImageData,textCallback:(lines:List<String>)->Unit) = internalImageAnalyticGateway.analyze(imageData, textCallback)

    fun getResult() = internalImageAnalyticGateway.getResult()
}