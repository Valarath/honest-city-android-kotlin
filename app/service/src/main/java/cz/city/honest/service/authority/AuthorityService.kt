package cz.city.honest.service.authority

import cz.city.honest.dto.ExchangeRate
import cz.city.honest.external.AuthorityServerSource
import cz.city.honest.service.BaseService
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable

class AuthorityService(
    private val authorityRepository: cz.city.honest.repository.authority.AuthorityRepository,
    private val authorityServerSource: AuthorityServerSource
) : BaseService(), PublicUpdatable {

    fun getAuthority(): Observable<ExchangeRate> =
        authorityRepository
            .get()
            .toObservable()

    override fun update(): Observable<Unit> =
        authorityRepository.delete()
            .doOnTerminate { getNewData().subscribe() }
            .onErrorComplete()
            .map { }

    private fun getNewData() = authorityServerSource
        .getRate()
        .flatMap { authorityRepository.insert(it.exchangeRate) }
}