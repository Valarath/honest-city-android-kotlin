package cz.city.honest.application.android.service.provider

import cz.city.honest.application.model.service.UserProvider
import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Observable
import java.util.*

class AndroidUserProvider:UserProvider {

    override fun provide(): Observable<User> = Observable.just(User(UUID.randomUUID().toString(), "blbštejn", 82))

}