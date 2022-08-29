package cz.city.honest.service.authority

import cz.city.honest.dto.ExchangeRate
import cz.city.honest.service.BaseService
import cz.city.honest.service.gateway.external.ExternalAuthorityGateway
import cz.city.honest.service.gateway.internal.InternalAuthorityGateway
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable

class AuthorityService(
    private val internalAuthorityGateway: InternalAuthorityGateway,
    private val externalAuthorityGateway: ExternalAuthorityGateway
) : BaseService(), PublicUpdatable {

    fun getAuthority(): Observable<ExchangeRate> =
        internalAuthorityGateway.getAuthority()

    override fun update(): Observable<Unit> =
        internalAuthorityGateway.delete()
            .doOnTerminate { getNewData().subscribe() }
            .onErrorComplete()
            .map { }

    private fun getNewData() = externalAuthorityGateway
        .getRate()
        .flatMap { internalAuthorityGateway.insert(it) }
}