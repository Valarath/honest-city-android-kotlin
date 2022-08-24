package cz.city.honest.service.authorization

import com.facebook.AccessToken
import cz.city.honest.dto.FacebookLoginData
import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.AsyncSubject

class FacebookLoginDataProvider : LoginDataProvider<FacebookLoginData> {

    override fun provide(user: User): Single<FacebookLoginData> =
        AsyncSubject.create<AccessToken> { AccessToken.getCurrentAccessToken() }
            .map { getFreshAccessToken(it) }
            .filter { !isTokenInvalid(it) }
            .map { it!! }
            .map { FacebookLoginData(it.token,it.userId, user.id) }
            .toList()
            .map { it.first() }

    private fun getFreshAccessToken(accessToken: AccessToken?) = accessToken.apply {
        if (isTokenInvalid(accessToken))
            AccessToken.refreshCurrentAccessTokenAsync()
    }

    private fun isTokenInvalid(accessToken: AccessToken?) = accessToken == null || accessToken.isExpired
}