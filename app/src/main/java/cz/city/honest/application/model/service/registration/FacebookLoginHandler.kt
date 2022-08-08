package cz.city.honest.application.model.service.registration

import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.server.AuthorizationServerSource
import cz.city.honest.application.model.server.PostLoginRequest
import cz.city.honest.application.model.server.PostRegisterRequest
import cz.city.honest.application.model.service.UserService
import io.reactivex.rxjava3.core.Observable

class FacebookLoginHandler(
    private val serverSource: AuthorizationServerSource,
    private val userService: UserService
) : LoginHandler<FacebookLoginData>() {

    override fun login(user: User): Observable<User> = serverSource
        .login(PostLoginRequest(user))
        .toObservable()
        .flatMap { logUser(it.user) }

    override fun register(data: FacebookLoginData): Observable<User> = serverSource
        .register(
            PostRegisterRequest(
                data
            )
        )
        .toObservable()
        .flatMap { registerUser(it.user) }

    private fun logUser(user: User) = user.let { it.logged = true }
        .run { userService.update(user) }
        .map { user }

    private fun registerUser(user: User) = user.let { it.logged = true }
        .run { userService.insert(user) }
        .map { user }

}

data class FacebookLoginData(
    val accessToken: String = "",
    val facebookUserId: String,
    val userId:String
) : LoginData {
    override fun userId(): String = userId
}