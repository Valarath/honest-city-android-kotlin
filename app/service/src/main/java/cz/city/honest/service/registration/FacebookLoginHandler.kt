package cz.city.honest.service.registration

import cz.city.honest.dto.FacebookLoginData
import cz.city.honest.dto.User
import cz.city.honest.service.gateway.external.ExternalAuthorizationGateway
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.Observable

class FacebookLoginHandler(
    private val externalAuthorizationGateway: ExternalAuthorizationGateway,
    private val userService: UserService
) : LoginHandler<FacebookLoginData>() {

    override fun login(user: User): Observable<User> = externalAuthorizationGateway
        .login(user)
        .toObservable()
        .flatMap { logUser(it.user) }

    override fun register(data: FacebookLoginData): Observable<User> = externalAuthorizationGateway
        .register(data)
        .toObservable()
        .flatMap { registerUser(it.user) }

    private fun logUser(user: User) = user.let { it.logged = true }
        .run { userService.update(user) }
        .map { user }

    private fun registerUser(user: User) = user.let { it.logged = true }
        .run { userService.insert(user) }
        .map { user }

}
