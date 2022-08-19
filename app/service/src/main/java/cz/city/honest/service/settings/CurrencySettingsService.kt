package cz.city.honest.service.settings

import cz.city.honest.dto.CurrencySettings
import cz.city.honest.external.CurrencyServerSource
import cz.city.honest.service.BaseService
import cz.city.honest.service.update.PublicUpdatable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsService(
    private val currencySettingsRepository: cz.city.honest.repository.settings.CurrencySettingsRepository,
    private val currencyServerSource: CurrencyServerSource
) : BaseService(), PublicUpdatable {

    fun get() = currencySettingsRepository.get()

    override fun update(): Observable<Unit> = currencySettingsRepository.delete()
        .doOnTerminate { getNewData() }
        .onErrorComplete()
        .map { }

    private fun getNewData() = currencyServerSource
        .getCurrenciesSettings()
        .flatMap { insert(it.currencySettings) }
        .subscribe()

    fun update(settings: List<CurrencySettings>) =
        Observable.fromIterable(settings)
            .flatMap { currencySettingsRepository.update(it) }

    fun insert(settings: List<CurrencySettings>) =
        Observable.fromIterable(settings)
            .flatMap { currencySettingsRepository.insert(it) }

}