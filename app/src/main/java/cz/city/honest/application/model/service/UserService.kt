package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.gateway.server.GetUserSuggestionsRequest
import cz.city.honest.application.model.gateway.server.GetUserSuggestionsResponse
import cz.city.honest.application.model.gateway.server.UserServerSource
import cz.city.honest.application.model.repository.user.UserRepository
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.util.*

class UserService(
    val userServerSource: UserServerSource,
    val userProvider: UserProvider,
    val userSuggestionRepository: UserSuggestionRepository
) : Updatable {

    fun getUserData(): Observable<User> =
        Observable.just(User(UUID.randomUUID().toString(), "blbštejn", 82))

    override fun update(): Observable<Unit> = Observable.empty()
        /*userServerSource.getUserSuggestions(getGetUserSuggestionsRequest(userProvider.provide()))
            .flatMap { it.userSuggestions.values.flatten().map { } }

            .flatMap { userSuggestionRepository.insertList(it) }

    private fun getUserSuggestions(getUserSuggestionsResponse: GetUserSuggestionsResponse, user: User) =
        getUserSuggestionsResponse.userSuggestions
            .values
            .flatten()
            .map { UserSuggestion(user,it!!,)}*/

    private fun getGetUserSuggestionsRequest(user: User) = GetUserSuggestionsRequest(user.id)

}

interface UserProvider {

    fun provide(): Observable<User>

}