package cz.city.honest.application.model.service.authority

import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.server.AuthorityServerSource
import cz.city.honest.application.model.repository.authority.AuthorityRepository
import cz.city.honest.application.model.service.BaseService
import cz.city.honest.application.model.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable

class AuthorityService(
    private val authorityRepository: AuthorityRepository,
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