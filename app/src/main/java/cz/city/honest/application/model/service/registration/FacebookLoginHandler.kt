package cz.city.honest.application.model.service.registration

import com.facebook.AccessToken
import cz.city.honest.application.model.gateway.server.FacebookLoginServerSource
import cz.city.honest.application.model.gateway.server.PostLoginFacebookUserRequest
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.dto.User
import io.reactivex.rxjava3.core.Observable

class FacebookLoginHandler(
    private val serverSource: FacebookLoginServerSource,
    private val userRepository: UserRepository
) : LoginHandler<FacebookLoginData>() {

    override fun login(data: FacebookLoginData): Observable<User> = serverSource
        .loginFacebookUser(PostLoginFacebookUserRequest(accessToken = data.accessToken.token))
        .flatMap { logUser(it.user) }

    private fun logUser(user: User) =
        user.copy(logged = true)
            .run { userRepository.update(user) }
            .map { user }
}

data class FacebookLoginData(val accessToken: AccessToken) : LoginData