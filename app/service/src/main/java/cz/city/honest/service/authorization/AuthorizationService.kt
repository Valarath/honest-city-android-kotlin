package cz.city.honest.service.authorization

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.service.BaseService
import cz.city.honest.service.InternalAuthorizationGatewayProvider
import cz.city.honest.service.gateway.external.ExternalAuthorizationGateway
import cz.city.honest.service.gateway.external.ExternalTokenValidationGateway
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class AuthorizationService(
    private val externalTokenValidationGateway: ExternalTokenValidationGateway,
    private val externalAuthorizationGateway: ExternalAuthorizationGateway,
    private val userService: UserService,
    private val internalAuthorizationGateways: Map<String, InternalAuthorizationGateway<out LoginData>>
) : BaseService() {

    fun getUserToken() = userService
        .getLoggedUser()
        .flatMap { getAuthenticationToken(it) }

    fun login(user: User) =
        externalAuthorizationGateway.login(user)
            .toObservable()
            .flatMap { loginResponse -> login(loginResponse.user,loginResponse.accessToken).map { loginResponse } }

    fun logout() = userService.getLoggedUser()
        .flatMap { getInternalAuthorizationGateway(it).logout(it) }
        .flatMap {  userService.update(it)}

    fun register(loginData: LoginData) =
        externalAuthorizationGateway
            .register(loginData)
            .toObservable()
            .flatMap { getInternalAuthorizationGateway(it.user).register(it.user, it.accessToken) }

    fun getAuthenticationToken(user: User) = getInternalAuthorizationGateway(user)
        .getAuthenticationToken(user)
        .switchIfEmpty(Maybe.fromObservable(login(user).map { it.accessToken }))
        .toObservable()
        .flatMap {getUnexpiredToken(it,user) }


    private fun login(user:User, accessToken: String) =
        getInternalAuthorizationGateway(user)
            .login(user, accessToken)

    private fun getUnexpiredToken(encodedToken: String, user: User) =
        externalTokenValidationGateway.isValid(encodedToken)
            .flatMap {
                if(it)
                    Observable.just(encodedToken)
                else
                    login(user).map { it.accessToken }
            }

    private fun getInternalAuthorizationGateway(user: User) =
        InternalAuthorizationGatewayProvider.provide(
            internalAuthorizationGateways,
            user.loginData
        )
}