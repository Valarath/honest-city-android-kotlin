package cz.city.honest.repository.user

import cz.city.honest.dto.UserSuggestion
import cz.city.honest.service.gateway.internal.InternalUserSuggestionGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class UserSuggestionService(private val userSuggestionRepository: UserSuggestionRepository) :
    InternalUserSuggestionGateway {

    override fun getUserSuggestions(id: String): Flowable<UserSuggestion> =
        userSuggestionRepository.get(listOf(id))

    override fun update(userSuggestion: UserSuggestion): Observable<Unit> =
        userSuggestionRepository.update(userSuggestion)
            .map { }

    override fun suggest(userSuggestion: UserSuggestion): Observable<Unit> =
        userSuggestionRepository.insert(userSuggestion)
            .map {  }

    override fun remove(userSuggestion: UserSuggestion): Observable<Unit> =
        userSuggestionRepository.delete(userSuggestion)
            .map {  }
}