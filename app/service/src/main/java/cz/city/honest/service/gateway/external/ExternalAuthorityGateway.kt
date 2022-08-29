package cz.city.honest.service.gateway.external

import cz.city.honest.dto.ExchangeRate
import io.reactivex.rxjava3.core.Observable

interface ExternalAuthorityGateway {

    fun getRate(): Observable<ExchangeRate>
}