package cz.city.honest.application.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.LoginDataUser
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.authorization.AuthorizationService
import javax.inject.Inject

class LoginViewModel @Inject constructor(private var userService: UserService,private var authorizationService: AuthorizationService) : ScheduledViewModel(){

    val loggedUser: MutableLiveData<User> = MutableLiveData()

    init {
        schedule {
            getUser().subscribe { loggedUser.postClearValue(it) }
        }
    }

    fun registerUser(loginData: LoginData) =
        authorizationService.register(loginData)
            .subscribe ()

    fun loginUser(user: LoginDataUser) =
        authorizationService.login(user)
            .subscribe ()

    private fun getUser() = userService.getUserData()
}
