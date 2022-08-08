package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.server.UserServerSource
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.application.model.service.update.PrivateUpdatable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class UserService(
    private val userServerSource: UserServerSource,
    private val userSuggestionRepository: UserSuggestionRepository,
    private val userRepository: UserRepository
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