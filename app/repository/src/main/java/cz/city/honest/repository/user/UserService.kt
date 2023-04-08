package cz.city.honest.repository.user

import cz.city.honest.dto.User
import cz.city.honest.service.gateway.internal.InternalUserGateway
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class UserService(private val userRepository: UserRepository) : InternalUserGateway {

    override fun getUserDataAsMaybe(): Maybe<User> = userRepository.getLoggedUser()

    override fun getUserDataAsMaybe(username:String): Maybe<User> = userRepository.getByUsername(username)

    override fun getUser(
        providerUserId: String
    ): Maybe<User> =
        userRepository
            .get(providerUserId)

    override fun update(user: User): Observable<Unit> =
        userRepository.update(user)
            .map { }

    override fun insert(user: User): Observable<Unit> =
        userRepository.insert(user)
            .map { }
}