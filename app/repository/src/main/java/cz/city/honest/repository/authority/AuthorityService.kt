package cz.city.honest.repository.authority

import cz.city.honest.dto.ExchangeRate
import cz.city.honest.service.gateway.internal.InternalAuthorityGateway
import io.reactivex.rxjava3.core.Observable

class AuthorityService(private val authorityRepository: AuthorityRepository) : InternalAuthorityGateway {

    override fun getAuthority(): Observable<ExchangeRate> =
        authorityRepository.get()
            .toObservable()

    override fun delete(): Observable<Unit> =
        authorityRepository.delete()
            .map {  }

    override fun insert(entity: ExchangeRate): Observable<Unit> =
        authorityRepository.insert(entity)
            .map {  }
}