package cz.honest.city.internal.provider.rate

import com.google.mlkit.vision.common.InputImage
import cz.city.honest.analyzer.rate.ExchangeRateAnalyzer
import cz.city.honest.dto.ExchangeRate
import cz.honest.city.internal.provider.ImageProvider
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class ImageExchangeRateProvider(
    private val exchangeRateAnalyzers: List<ExchangeRateAnalyzer>
) : ImageProvider<ExchangeRate>() {

    override fun mapToResult(textLines: List<String>, image: InputImage) {
        Maybe.mergeArray(*exchangeRateAnalyzers.map { it.analyze(textLines) }
            .toTypedArray())
            .subscribe {
                result = Observable.just(it)
            }
    }

}