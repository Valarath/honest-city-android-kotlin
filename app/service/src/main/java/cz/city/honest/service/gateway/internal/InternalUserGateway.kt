package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

interface InternalUserGateway {

    fun getUserDataAsMaybe(): Maybe<User>

    fun getUser(providerUserId: String, providerDataType: Class<out LoginData>): Maybe<User>

    fun update(user: User): Observable<Unit>

    fun insert(user: User): Observable<Unit>

    fun getUserDataAsMaybe(username: String): Maybe<User>
}