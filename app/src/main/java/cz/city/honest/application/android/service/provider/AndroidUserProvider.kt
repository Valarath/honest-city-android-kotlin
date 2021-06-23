package cz.city.honest.application.android.service.provider

import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.service.UserProvider
import cz.city.honest.application.model.dto.User
import io.reactivex.rxjava3.core.Observable

class AndroidUserProvider(private val userRepository: UserRepository) : UserProvider {

    override fun provide(): Observable<User> = userRepository.getLoggedUser().toObservable();

}