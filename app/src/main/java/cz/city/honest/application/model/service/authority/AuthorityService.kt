package cz.city.honest.application.model.service.authority

import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.dto.ExchangeRateValues
import cz.city.honest.application.model.dto.Rate
import cz.city.honest.application.model.dto.Watched
import cz.city.honest.application.model.gateway.server.AuthorityServerSource
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.service.BaseService
import cz.city.honest.application.model.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate

class AuthorityService(
    val authorityRepository: AuthorityRepository,
    val authorityServerSource: AuthorityServerSource
) : BaseService(), PublicUpdatable {

    fun getAuthority(): Observable<ExchangeRate> =
        Observable.just(getMockExchangeRate())
        /*authorityRepository
            .get()
            .toObservable()*/

    private fun getMockExchangeRate(): ExchangeRate = ExchangeRate(
        "",
        Watched(LocalDate.now(), LocalDate.MAX),
        mutableSetOf(
            Rate("cze", ExchangeRateValues(1.0)),
            Rate("eur", ExchangeRateValues(22.0)),
            Rate("usd", ExchangeRateValues(22.0)),
            Rate("aud", ExchangeRateValues(10.0))
        )
    )

    override fun update(): Observable<Unit> =
        authorityRepository.delete()
            .flatMap { authorityServerSource.getRate()}
            .flatMap { authorityRepository.insert(it.exchangeRate)}
            .map { }
}