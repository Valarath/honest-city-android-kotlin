package cz.city.honest.application.model.service.settings

import cz.city.honest.application.model.dto.CurrencySetting
import cz.city.honest.application.model.repository.settings.CurrencySettingsRepository
import cz.city.honest.application.model.service.BaseService
import cz.city.honest.application.model.service.Updatable
import io.reactivex.rxjava3.core.Observable

class CurrencySettingsService(private val currencySettingsRepository: CurrencySettingsRepository):BaseService(),Updatable{

    fun get()=currencySettingsRepository.get()

    override fun update(): Observable<Unit> {
        TODO("Not yet implemented")
    }

    fun update(settings:List<CurrencySetting>)=currencySettingsRepository.updateList(settings)

    fun insert(settings:List<CurrencySetting>)=currencySettingsRepository.insertList(settings)

}