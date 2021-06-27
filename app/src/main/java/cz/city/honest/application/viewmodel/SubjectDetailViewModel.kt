package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.service.authority.AuthorityService
import javax.inject.Inject

class SubjectDetailViewModel @Inject constructor(authorityService: AuthorityService) :
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