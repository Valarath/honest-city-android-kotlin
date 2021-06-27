package cz.city.honest.application.model.service.settings

import cz.city.honest.application.model.dto.CurrencySetting
import cz.city.honest.application.model.repository.settings.CurrencySettingsRepository
import cz.city.honest.application.model.service.BaseService
import cz.city.honest.application.model.service.update.PrivateUpdatable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsService(
    private val currencySettingsRepository: CurrencySettingsRepository
) : BaseService(), PrivateUpdatable {

    fun get() = currencySettingsRepository.get()

    override fun update(accessToken:String): Observable<Unit> {
        TODO("Not yet implemented")
    }

    fun update(settings: List<CurrencySetting>) = currencySettingsRepository.updateList(settings)

    fun insert(settings: List<CurrencySetting>) = currencySettingsRepository.insertList(settings)

}