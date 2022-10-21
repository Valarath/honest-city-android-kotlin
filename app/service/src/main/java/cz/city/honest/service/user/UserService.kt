package cz.city.honest.service.user

import cz.city.honest.dto.*
import cz.city.honest.service.BaseService
import cz.city.honest.service.gateway.external.ExternalUserGateway
import cz.city.honest.service.gateway.internal.InternalAuthorizationGateway
import cz.city.honest.service.gateway.internal.InternalUserGateway
import cz.city.honest.service.gateway.internal.InternalUserSuggestionGateway
import cz.city.honest.service.update.PrivateUpdatable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class UserService(
    private val externalUserGateway: ExternalUserGateway,
    private val internalUserSuggestionGateway: InternalUserSuggestionGateway,
    private val internalUserGateway: InternalUserGateway
) : BaseService(), PrivateUpdatable {

    fun getLoggedUser(): Observable<User> = getUserDataAsMaybe()
        .toObservable()

    fun getUserDataAsMaybe(): Maybe<User> = internalUserGateway.getUserDataAsMaybe()

    fun getUser(providerUserId: String, providerDataType: Class<out LoginData>) =
        internalUserGateway
            .getUser(providerUserId, providerDataType)

    fun update(user: User) = internalUserGateway.update(user)

    fun insert(user: User) = internalUserGateway.insert(user)

    override fun update(accessToken: String): Observable<Unit> =
        getLoggedUser()
            .flatMap { getUserSuggestions(it, accessToken) }
            .flatMap { insertUserSuggestions(it) }
            //.onErrorComplete()

    private fun insertUserSuggestions(userSuggestions:List<UserSuggestion>) =
        Observable.fromIterable(userSuggestions)
            .flatMap {internalUserSuggestionGateway.suggest(it)  }
            .map {  }

    private fun getUserSuggestions(user: User, accessToken: String) =
        externalUserGateway.getUserSuggestions(user, accessToken)
            .map { it.userSuggestions.values.flatten() }
            .flatMap { Observable.fromIterable(it) }
            .map { UserSuggestion(user, it!!, getUserSuggestionMetadata()) }
            .toList()
            .toObservable()


    private fun getUserSuggestionMetadata() =
        UserSuggestionMetadata(processed = true, markAs = UserSuggestionStateMarking.NEW)

}

interface UserProvider {

    fun provide(): Observable<User>

}