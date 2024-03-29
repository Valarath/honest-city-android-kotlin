package cz.city.honest.application.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.authorization.AuthorizationService
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private var userService: UserService,
    private var authorizationService: AuthorizationService
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
        providerDataType: Class<out LoginData>,
        subscribeHandler: LoginUserSubscribeHandler
    ) = userService
        .getUser(providerUserId, providerDataType)
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