package cz.city.honest.service.registration

import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import io.reactivex.rxjava3.core.Observable

abstract class LoginHandler<DATA : LoginData> {

    abstract fun login(user: User): Observable<User>
    abstract fun register(data: DATA): Observable<User>
}
