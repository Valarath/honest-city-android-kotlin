package cz.city.honest.application.model.service.registration

import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.LoginDataUser
import cz.city.honest.application.model.dto.User
import io.reactivex.rxjava3.core.Observable

abstract class LoginHandler<DATA : LoginData> {

    abstract fun login(user: LoginDataUser): Observable<User>
    abstract fun register(data: DATA): Observable<User>
}
