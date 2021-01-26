package cz.city.honest.application.model.service

import cz.city.honest.application.model.gateway.server.AuthorityServerSource
import cz.city.honest.application.model.repository.AuthorityRepository
import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.ExchangeRateValues
import cz.city.honest.mobile.model.dto.Rate
import cz.city.honest.mobile.model.dto.Watched
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class AuthorityService(
    val authorityRepository: AuthorityRepository,
    val authorityServerSource: AuthorityServerSource
) : BaseService(), Updatable {

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

    override fun update(): Observable<Unit> {
        TODO("Not yet implemented")
    }


}