package cz.city.honest.service.gateway.external

import io.reactivex.rxjava3.core.Observable

interface ExternalTokenValidationGateway {

    fun isValid(token: String): Observable<Boolean>
}