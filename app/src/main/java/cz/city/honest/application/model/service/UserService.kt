package cz.city.honest.application.model.service

import cz.city.honest.application.model.gateway.server.UserServerSource
import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Observable

class UserService( val userServerSource: UserServerSource):Updatable {

    fun getUserData(): Observable<User> = Observable.just(User(66, "blbštejn", 82))
    override fun update(): Observable<Unit> {
        TODO("Not yet implemented")
    }


}