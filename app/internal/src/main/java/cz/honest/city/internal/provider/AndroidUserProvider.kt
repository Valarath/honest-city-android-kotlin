package cz.honest.city.internal.provider

import cz.city.honest.dto.User
import cz.city.honest.service.provider.UserProvider
import cz.city.honest.service.user.UserService
import io.reactivex.rxjava3.core.Observable

class AndroidUserProvider(
    private val userService: UserService
) : UserProvider {

    override fun provide(): Observable<User> = userService.getLoggedUser()

}