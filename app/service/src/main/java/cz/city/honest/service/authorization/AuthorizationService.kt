package cz.city.honest.service.authorization

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.external.AuthorizationServerSource
import cz.city.honest.external.PostLoginRequest
import cz.city.honest.external.PostLoginResponse
import cz.city.honest.service.BaseService
import cz.city.honest.service.LoginHandlerProvider
import cz.city.honest.service.user.UserService
import cz.city.honest.service.registration.LoginHandler
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

class AuthorizationService(
    private val authorizationServerSource: AuthorizationServerSource,
    private val userService: UserService,
    private val loginHandlers: Map<String, LoginHandler<out LoginData>>
) : BaseService() {

    fun getUserToken(): Maybe<String> = userService
        .getUserDataAsMaybe()
        .flatMap { authorizeUser(it) }
        .map { it.accessToken }

    fun login(user: User) =
        LoginHandlerProvider.provide(loginHandlers, user.loginData)
            .login(user)

    fun register(loginData: LoginData) =
        LoginHandlerProvider.provide(loginHandlers, loginData)
            .register(loginData)


    private fun authorizeUser(user: User) =
        Single.just(user)
            .map { toPostLoginRequest(user) }
            .flatMap { authorizationServerSource.login(it) }
            .flatMap { updateUserData(it) }
            .toMaybe()

    private fun updateUserData(response: PostLoginResponse) = userService
        .update(getUser(response))
        .map { response }
        .toList()
        .map { it.first() }

    private fun getUser(response: PostLoginResponse) =
        response.user
            .apply { this.logged = true }

    private fun toPostLoginRequest(user: User) =
        PostLoginRequest(user)


}

interface LoginDataProvider<LOGIN_DATA : LoginData> {
    fun provide(user: User): Single<LOGIN_DATA>
}