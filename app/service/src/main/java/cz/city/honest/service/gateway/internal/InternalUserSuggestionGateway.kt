package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.UserSuggestion
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InternalUserSuggestionGateway {

    fun getUserSuggestions(id: String): Flowable<UserSuggestion>

    fun update(userSuggestion: UserSuggestion): Observable<Unit>

    fun suggest(userSuggestion: UserSuggestion): Observable<Unit>

    fun remove(userSuggestion: UserSuggestion): Observable<Unit>

}