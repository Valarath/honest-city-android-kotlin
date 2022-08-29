package cz.city.honest.external

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.service.gateway.external.ExternalAuthorizationGateway
import cz.city.honest.service.gateway.external.LoginResponse
import cz.city.honest.service.gateway.external.RegisterResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

class AuthorizationServerSourceService(
    private val authorityServerSource: AuthorizationServerSource
) : ExternalAuthorizationGateway {

    override fun register(loginData: LoginData): Single<RegisterResponse> =
        authorityServerSource.register(PostRegisterRequest(loginData))
            .map { RegisterResponse(it.accessToken, it.user) }

    override fun login(user: User): Single<LoginResponse> =
        authorityServerSource.login(PostLoginRequest(user))
            .map { LoginResponse(it.accessToken,it.user) }
}

interface AuthorizationServerSource {

    @POST(AuthorizationEndpointsUrl.REGISTER)
    fun register(@Body request: PostRegisterRequest): Single<PostRegisterResponse>

    @POST(AuthorizationEndpointsUrl.LOGIN)
    fun login(@Body request: PostLoginRequest): Single<PostLoginResponse>

}

data class PostLoginResponse(val accessToken: String, val user: User)

data class PostLoginRequest(val user: User)

data class PostRegisterResponse(val accessToken: String, val user: User)

data class PostRegisterRequest(val loginData: LoginData)

object AuthorizationEndpointsUrl {

    private const val AUTHORIZATION_PREFIX: String = EndpointsUrl.PUBLIC + "/authorization"
    const val REGISTER = "$AUTHORIZATION_PREFIX/register"
    const val LOGIN = "$AUTHORIZATION_PREFIX/login"

}