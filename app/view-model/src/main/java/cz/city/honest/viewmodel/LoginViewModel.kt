package cz.city.honest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.service.user.UserService
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private var userService: UserService,
    private var authorizationService: cz.city.honest.service.authorization.AuthorizationService
) : ScheduledObservableViewModel() {

    val loggedUser: LiveData<User> = LiveDataReactiveStreams.fromPublisher<User> (getLoggedUser().toFlowable())

    fun registerUser(loginData: LoginData) =
        authorizationService.register(loginData)
            .subscribe()

    fun loginUser(user: User) =
        authorizationService.login(user)
            .subscribe()

    fun loginUser(
        providerUserId: String,
        subscribeHandler: LoginUserSubscribeHandler
    ) = userService
        .getUser(providerUserId)
        .subscribe(
            { subscribeHandler.loginUser.invoke(it) },
            { subscribeHandler.exceptionHandler.invoke(it) },
            { subscribeHandler.registerUser.invoke() }
        )

    private fun getLoggedUser() = userService.getUserDataAsMaybe()

}

data class LoginUserSubscribeHandler(
    val loginUser: ((user: User) -> Unit),
    val exceptionHandler: ((exception: Throwable) -> Unit),
    val registerUser: (() -> Unit)
)