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
    val loggedUser: MutableLiveData<User> = MutableLiveData()

    init {
        schedule {
            authorityService.getAuthority().subscribe {
                authorityRate.postClearValue(it)
            }
            getUser().subscribe { loggedUser.postClearValue(it) }
        }
    }

    private fun getUser() = userService.getUserDataAsMaybe()
}