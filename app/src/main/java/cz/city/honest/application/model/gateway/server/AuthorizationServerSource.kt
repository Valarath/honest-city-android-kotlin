package cz.city.honest.application.model.gateway.server

import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.User
import io.reactivex.rxjava3.core.Single
import retrofit2.http.POST

interface AuthorizationServerSource {

    @POST(AuthorizationEndpointsUrl.REGISTER)
    fun register(request:PostRegisterRequest): Single<PostRegisterResponse>

    @POST(AuthorizationEndpointsUrl.LOGIN)
    fun login(request:PostLoginRequest): Single<PostLoginResponse>

}

data class PostLoginResponse(val accessToken:String, val user:User)
data class PostLoginRequest(val user:User)
data class PostRegisterResponse(val accessToken:String, val user:User)
data class PostRegisterRequest(val loginData: LoginData)

object AuthorizationEndpointsUrl{

        private const val AUTHORIZATION_PREFIX: String = EndpointsUrl.PUBLIC + "/authorization"
        const val REGISTER = "$AUTHORIZATION_PREFIX/register"
        const val LOGIN = "$AUTHORIZATION_PREFIX/login"

}