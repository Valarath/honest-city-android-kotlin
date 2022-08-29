package cz.city.honest.repository.settings

import cz.city.honest.dto.CurrencySettings
import cz.city.honest.service.gateway.internal.InternalCurrencySettingsGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsService(private val currencySettingsRepository: CurrencySettingsRepository) :
    InternalCurrencySettingsGateway {

    override fun insert(entity: CurrencySettings): Observable<Unit> =
        currencySettingsRepository.insert(entity)
            .map { }

    override fun update(entity: CurrencySettings): Observable<Unit> =
        currencySettingsRepository.update(entity)
            .map {  }

    override fun delete(): Observable<Unit> = currencySettingsRepository.delete()
        .map {  }

    override fun get(): Flowable<CurrencySettings> = currencySettingsRepository.get()
}