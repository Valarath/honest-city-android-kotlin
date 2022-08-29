package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.ExchangeRate
import io.reactivex.rxjava3.core.Observable

interface InternalAuthorityGateway {

    fun getAuthority(): Observable<ExchangeRate>

    fun delete(): Observable<Unit>

    fun insert(entity: ExchangeRate): Observable<Unit>
}