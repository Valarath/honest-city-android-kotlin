package cz.city.honest.application.model.service.authorization

import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.LoginDataUser
import cz.city.honest.application.model.dto.LoginProvider
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.gateway.server.AuthorizationServerSource
import cz.city.honest.application.model.gateway.server.PostLoginRequest
import cz.city.honest.application.model.gateway.server.PostLoginResponse
import cz.city.honest.application.model.service.BaseService
import cz.city.honest.application.model.service.LoginHandlerProvider
import cz.city.honest.application.model.service.UserProvider
import cz.city.honest.application.model.service.UserService
import cz.city.honest.application.model.service.registration.LoginHandler
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

class AuthorizationService(
    private val authorizationServerSource: AuthorizationServerSource,
    private val userService: UserService,
    private val loginHandlers:Map<String, LoginHandler<out LoginData>>,
    private val loginDataProviders: Map<LoginProvider,LoginDataProvider<out LoginData>>
) : BaseService() {

    fun getUserToken(): Maybe<String> = userService
        .getUserDataAsMaybe()
        .flatMap { logUser(it) }
        .map { it.accessToken }

    fun login( user: LoginDataUser) =
        LoginHandlerProvider.provide(loginHandlers,user.loginData)
            .login(user)

    fun register( loginData: LoginData) =
        LoginHandlerProvider.provide(loginHandlers,loginData)
            .register(loginData)


    private fun logUser(user: User) =
        loginDataProviders[user.loginProvider]!!.provide()
            .map { toPostLoginRequest(user, it) }
            .flatMap { authorizationServerSource.login(it) }
            .flatMap { updateUserData(it) }
            .toMaybe()

    private fun updateUserData(response: PostLoginResponse) = userService
        .update(response.user)
        .map { response }
        .toList()
        .map { it.first() }

    private fun toPostLoginRequest(user: User, loginData: LoginData) =
        PostLoginRequest(toLoginDataUser(user, loginData))

    private fun toLoginDataUser(user: User, loginData: LoginData) =
        LoginDataUser(
            id = user.id,
            username = user.username,
            score = user.score,
            logged = user.logged,
            loginData = loginData,
            loginProvider = user.loginProvider
        )


}

interface LoginDataProvider<LOGIN_DATA : LoginData> {
    fun provide(): Single<LOGIN_DATA>
}