package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.gateway.server.PostSuggestRequest
import cz.city.honest.application.model.gateway.server.RemoveSuggestionRequest
import cz.city.honest.application.model.gateway.server.SuggestionServerSource
import cz.city.honest.application.model.repository.user.UserSuggestionRepository
import cz.city.honest.mobile.model.dto.User
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class UserSuggestionService(
    val suggestionServerSource: SuggestionServerSource,
    val userSuggestionRepository: UserSuggestionRepository,
    val userProvider: UserProvider
) : Updatable {

    override fun update(): Observable<Unit> =
        userProvider.provide()
            .concatMap { removeSuggestions(it);suggestSuggestions(it) }

    private fun removeSuggestions(user: User): Observable<Unit> =
        userSuggestionRepository.getForDelete(listOf(user.id))
            .toList()
            .toObservable()
            .flatMap { removeSuggestions(it) }
            .map {}

    private fun removeSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Flowable.fromIterable(userSuggestions)
            .map { it.suggestion }
            .toList()
            .toObservable()
            .flatMap { suggestionServerSource.remove(RemoveSuggestionRequest(it)) }
            .flatMap { userSuggestionRepository.deleteList(userSuggestions) }

    private fun suggestSuggestions(user: User): Observable<Unit> =
        userSuggestionRepository.getNew(listOf(user.id))
            .toList()
            .toObservable()
            .flatMap { suggestSuggestions(it) }
            .map {}

    private fun suggestSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Flowable.fromIterable(userSuggestions)
            .map { it.suggestion }
            .toList()
            .toObservable()
            .flatMap { suggestionServerSource.suggest(PostSuggestRequest(it)) }
            .flatMap { userSuggestionRepository.updateList(userSuggestions) }
}