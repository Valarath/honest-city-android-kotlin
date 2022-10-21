package cz.honest.city.internal.authentication

import android.content.Context
import cz.city.honest.dto.FacebookLoginData
import cz.city.honest.dto.User
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.Observable

class FacebookLoginHandler(
    private val userService: UserService,
    context: Context
) : LoginHandler<FacebookLoginData>(context) {

    override fun login(user: User, accessToken: String): Observable<User> =
        updateInAccountManager(user, accessToken)
            .flatMap { logUser(it) }
    

    override fun register(user: User, accessToken: String): Observable<User> =
        registerAccountInAccountManager(user, accessToken)
            .flatMap { registerUser(it) }

    private fun logUser(user: User) = Observable.just(user.let { it.logged = true })
        .map { userService.update(user) }
        .map { user }

    private fun registerUser(user: User) = Observable.just(user.let { it.logged = true })
        .map { userService.insert(user) }
        .map { user }

}