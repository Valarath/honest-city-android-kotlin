package cz.city.honest.application.model.service

import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Observable
import javax.inject.Singleton

@Singleton
class UserService {

    fun getUserData(): Observable<User> = Observable.just(User(66, "blbštejn", 82))


}