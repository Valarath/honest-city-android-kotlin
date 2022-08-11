package cz.city.honest.application.analyzer

import cz.city.honest.application.model.dto.ExchangeRate
import io.reactivex.rxjava3.core.Maybe

interface ExchangeRateAnalyzer {

    fun analyze(lines: List<String>): Maybe<ExchangeRate>

}