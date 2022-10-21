package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface InternalAuthorizationGateway<DATA : LoginData> {

    fun login(user: User, accessToken: String): Observable<User>

    fun register(user: User,accessToken:String): Observable<User>

    fun getAuthenticationToken(user: User): Maybe<String>

}