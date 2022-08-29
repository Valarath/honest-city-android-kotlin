package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.CurrencySettings
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InternalCurrencySettingsGateway {

    fun insert(entity: CurrencySettings): Observable<Unit>

    fun update(entity: CurrencySettings): Observable<Unit>

    fun delete(): Observable<Unit>

    fun get(): Flowable<CurrencySettings>
}