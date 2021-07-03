package cz.city.honest.application.android.service.provider

import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.service.UserProvider
import cz.city.honest.application.model.service.UserService
import io.reactivex.rxjava3.core.Observable

class AndroidUserProvider(
    private val userService: UserService
) : UserProvider {

    override fun provide(): Observable<User> = userService.getLoggedUser()

}