package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.AnalyzeImageData
import io.reactivex.rxjava3.core.Observable

interface InternalImageNameAnalyticGateway {

    fun analyze(
        imageData: AnalyzeImageData,
        textCallback: (lines: List<String>) -> Unit
    ): Observable<Unit>

    fun getResult(): Observable<String>
}