package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.dto.UserSuggestionMetadata
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.model.gateway.server.GetUserSuggestionsRequest
import cz.city.honest.application.model.gateway.server.GetUserSuggestionsResponse
import cz.city.honest.application.model.gateway.server.UserServerSource
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.application.model.service.registration.LoginData
import cz.city.honest.application.model.service.registration.LoginHandler
import cz.city.honest.application.model.dto.LoginProvider
import cz.city.honest.application.model.dto.User
import io.reactivex.rxjava3.core.Observable

class UserService(
    private val userServerSource: UserServerSource,
    private val userProvider: UserProvider,
    private val userSuggestionRepository: UserSuggestionRepository,
    private val loginProviders:Map<LoginProvider,LoginHandler<out LoginData>>
) : Updatable {

    fun getUserData(): Observable<User> =
       userProvider.provide()

    override fun update(): Observable<Unit> = Observable.empty()
        /*userServerSource.getUserSuggestions(getGetUserSuggestionsRequest(userProvider.provide()))
            .flatMap { it.userSuggestions.values.flatten().map { } }

            .flatMap { userSuggestionRepository.insertList(it) }*/

    private fun getUserSuggestions(getUserSuggestionsResponse: GetUserSuggestionsResponse, user: User) =
        getUserSuggestionsResponse.userSuggestions
            .values
            .flatten()
            .map { UserSuggestion(user,it!!, getUserSuggestionMetadata()) }

    private fun getUserSuggestionMetadata() =
        UserSuggestionMetadata(processed = true, markAs = UserSuggestionStateMarking.NEW)

    private fun getGetUserSuggestionsRequest(user: User) = GetUserSuggestionsRequest(user.id)

    fun login(loginProvider: LoginProvider, loginData: LoginData) =
       LoginHandlerProvider.provide(loginProviders,loginProvider)
        .login(loginData)

}

interface UserProvider {

    fun provide(): Observable<User>

}