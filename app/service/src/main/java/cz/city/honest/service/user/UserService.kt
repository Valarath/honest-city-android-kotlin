package cz.city.honest.service.user

import cz.city.honest.external.UserServerSource
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.dto.*
import cz.city.honest.service.BaseService
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class UserService(
    private val userServerSource: UserServerSource,
    private val userSuggestionRepository: cz.city.honest.repository.user.UserSuggestionRepository,
    private val userRepository: cz.city.honest.repository.user.UserRepository
) : BaseService(), PrivateUpdatable {

    fun getLoggedUser(): Observable<User> = userRepository.getLoggedUser().toObservable()

    fun getUserDataAsMaybe(): Maybe<User> = userRepository.getLoggedUser()

    fun getUser(providerUserId: String, providerDataType: Class<out LoginData>) =
        userRepository
            .get(providerUserId, providerDataType)

    fun update(user: User) = userRepository.update(user)

    fun insert(user: User) = userRepository.insert(user)

    override fun update(accessToken: String): Observable<Unit> =
        getLoggedUser()
            .flatMap { getUserSuggestions(it, accessToken) }
            .flatMap { insertUserSuggestions(it) }
            //.onErrorComplete()

    private fun insertUserSuggestions(userSuggestions:List<UserSuggestion>) =
        Observable.fromIterable(userSuggestions)
            .flatMap {userSuggestionRepository.insert(it)  }
            .map {  }

    private fun getUserSuggestions(user: User, accessToken: String) =
        userServerSource.getUserSuggestions(getGetUserSuggestionsRequest(user), accessToken)
            .map { it.userSuggestions.values.flatten() }
            .flatMap { Observable.fromIterable(it) }
            .map { UserSuggestion(user, it!!, getUserSuggestionMetadata()) }
            .toList()
            .toObservable()


    private fun getUserSuggestionMetadata() =
        UserSuggestionMetadata(processed = true, markAs = UserSuggestionStateMarking.NEW)

    private fun getGetUserSuggestionsRequest(user: User) = mapOf("userId" to user.id)

}

interface UserProvider {

    fun provide(): Observable<User>

}