package cz.city.honest.service.gateway.external

import cz.city.honest.dto.CurrencySettings
import io.reactivex.rxjava3.core.Observable

interface ExternalCurrencyGateway {

    fun getCurrenciesSettings(): Observable<List<CurrencySettings>>
}