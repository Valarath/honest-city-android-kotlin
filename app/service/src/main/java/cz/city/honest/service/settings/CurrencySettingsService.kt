package cz.city.honest.service.settings

import cz.city.honest.dto.CurrencySettings
import cz.city.honest.service.BaseService
import cz.city.honest.service.gateway.external.ExternalCurrencyGateway
import cz.city.honest.service.gateway.internal.InternalCurrencySettingsGateway
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsService(
    private val internalCurrencySettingsGateway: InternalCurrencySettingsGateway,
    private val externalCurrencyGateway: ExternalCurrencyGateway
) : BaseService(), PublicUpdatable {

    fun get() = internalCurrencySettingsGateway.get()

    override fun update(): Observable<Unit> = internalCurrencySettingsGateway.delete()
        .doOnTerminate { getNewData() }
        .onErrorComplete()
        .map { }

    private fun getNewData() = externalCurrencyGateway
        .getCurrenciesSettings()
        .flatMap { insert(it) }
        .subscribe()

    fun update(settings: List<CurrencySettings>) =
        Observable.fromIterable(settings)
            .flatMap { internalCurrencySettingsGateway.update(it) }

    fun insert(settings: List<CurrencySettings>) =
        Observable.fromIterable(settings)
            .flatMap { internalCurrencySettingsGateway.insert(it) }

}