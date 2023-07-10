package cz.city.honest.service.provider

import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Observable

interface UserProvider {

    fun provide(): Observable<User>

}