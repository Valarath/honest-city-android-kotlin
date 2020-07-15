package cz.city.honest.application.model.service

import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.ExchangeRateValues
import cz.city.honest.mobile.model.dto.Rate
import cz.city.honest.mobile.model.dto.Watched
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class AuthorityService : BaseService() {

    fun getAuthority(): Observable<ExchangeRate> = Observable.just(getMockExchangeRate())

    private fun getMockExchangeRate(): ExchangeRate = ExchangeRate(
        25,
        Watched(LocalDate.now(), LocalDate.MAX),
        mutableSetOf(
            Rate("CZE", ExchangeRateValues(1.0)),
            Rate("EUR", ExchangeRateValues(22.0)),
            Rate("USD", ExchangeRateValues(22.0))
        )
    )


}