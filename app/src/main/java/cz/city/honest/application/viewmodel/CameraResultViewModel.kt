package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.service.AuthorityService
import cz.city.honest.mobile.model.dto.ExchangeRate
import javax.inject.Inject

class CameraResultViewModel @Inject constructor(authorityService: AuthorityService) :
    ScheduledViewModel() {

    val authorityRate = MutableLiveData<ExchangeRate>()

    init {
        schedule {
            authorityService.getAuthority().subscribe {
                authorityRate.postClearValue(it)
            }
        }
    }
}