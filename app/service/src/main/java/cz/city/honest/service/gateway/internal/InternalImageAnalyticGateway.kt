package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.AnalyzeImageData
import cz.city.honest.dto.ExchangeRate
import io.reactivex.rxjava3.core.Observable

interface InternalImageAnalyticGateway {

    fun analyze(imageData: AnalyzeImageData,textCallback:(lines:List<String>)->Unit): Observable<Unit>

    fun getResult(): Observable<ExchangeRate>
}