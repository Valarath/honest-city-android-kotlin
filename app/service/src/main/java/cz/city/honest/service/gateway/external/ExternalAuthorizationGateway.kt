package cz.city.honest.service.gateway.external

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Single

interface ExternalAuthorizationGateway {

    fun register(loginData: LoginData): Single<RegisterResponse>

    fun login(user: User): Single<LoginResponse>
}

data class LoginResponse(val accessToken:String, val user: User)

data class RegisterResponse(val accessToken:String, val user: User)
