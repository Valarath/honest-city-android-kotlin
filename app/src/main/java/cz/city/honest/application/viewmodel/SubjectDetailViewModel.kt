package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.ExchangeRate
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.authority.AuthorityService
import javax.inject.Inject

class SubjectDetailViewModel @Inject constructor(authorityService: AuthorityService, val userService: UserService) :
    ScheduledViewModel() {

    val authorityRate = MutableLiveData<ExchangeRate>()
    var loggedUser: User? = null

    init {
        schedule {
            authorityService.getAuthority().subscribe {
                authorityRate.postClearValue(it)
            }
            getLoggedUser().subscribe({ loggedUser = it }, {}, { loggedUser = null })
        }
    }

    private fun getLoggedUser() = userService.getUserDataAsMaybe()
}