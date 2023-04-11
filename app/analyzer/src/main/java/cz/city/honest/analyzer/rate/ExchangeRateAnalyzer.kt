package cz.city.honest.analyzer.rate

import cz.city.honest.dto.ExchangeRate
import io.reactivex.rxjava3.core.Maybe

interface ExchangeRateAnalyzer {

    fun analyze(lines: List<String>): Maybe<ExchangeRate>

}