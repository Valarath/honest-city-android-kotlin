package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.registration.LoginData
import cz.city.honest.application.model.dto.LoginProvider
import cz.city.honest.application.model.dto.User
import javax.inject.Inject

class LoginViewModel @Inject constructor(private var userService: UserService) : ScheduledViewModel(){

    val loggedUser: MutableLiveData<User> = MutableLiveData()

    init {
        schedule {
            getUser().subscribe { loggedUser.postClearValue(it) }
        }
    }

    fun loginUser(loginData: LoginData, loginProvider: LoginProvider) =
        userService.login(loginProvider,loginData)
            .subscribe ()

    private fun getUser() = userService.getUserData()
}
