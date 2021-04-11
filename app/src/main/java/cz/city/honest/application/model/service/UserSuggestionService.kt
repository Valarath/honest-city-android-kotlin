package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.dto.UserSuggestionMetadata
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
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

    fun getUserSuggestions(id: String): Flowable<UserSuggestion> =
        userSuggestionRepository.get(listOf(id))

    fun delete(userSuggestion: UserSuggestion) =
        userSuggestionRepository.update(toDeleteStateSuggestion(userSuggestion))

    fun suggest(userSuggestion: UserSuggestion) =
        userSuggestionRepository.insert(userSuggestion)

    fun suggest(suggestion: Suggestion,markAs:UserSuggestionStateMarking) =
        toUserSuggestion(suggestion,markAs)
            .flatMap { suggest(it) }

    private fun toUserSuggestion(suggestion: Suggestion, markAs:UserSuggestionStateMarking) =
        userProvider.provide()
            .map { toUserSuggestion(it, suggestion, markAs) }

    private fun toUserSuggestion(
        user: User,
        suggestion: Suggestion,
        markAs: UserSuggestionStateMarking
    ) = UserSuggestion(
        user = user,
        suggestion = suggestion,
        metadata = toUserSuggestionMetadata(markAs)
    )

    private fun toDeleteStateSuggestion(userSuggestion: UserSuggestion) =
        UserSuggestion(
            user = userSuggestion.user,
            suggestion = userSuggestion.suggestion,
            metadata = getDeleteStateUserSuggestionMetadata()
        )

    private fun getDeleteStateUserSuggestionMetadata() =
        UserSuggestionMetadata(
            processed = false,
            markAs = UserSuggestionStateMarking.DELETE
        )

    private fun toUserSuggestionMetadata(markAs: UserSuggestionStateMarking) =
        UserSuggestionMetadata(
            processed = false,
            markAs = markAs
        )

    private fun removeSuggestions(user: User): Observable<Unit> =
        userSuggestionRepository.get(listOf(user.id))
            .filter { it.metadata.markAs == UserSuggestionStateMarking.DELETE }
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
        userSuggestionRepository.get(listOf(user.id))
            .filter { isSuggestionNew(it) }
            .toList()
            .toObservable()
            .flatMap { suggestSuggestions(it) }
            .map {}

    private fun isSuggestionNew(userSuggestion: UserSuggestion) =
        userSuggestion.metadata.markAs == UserSuggestionStateMarking.NEW && !userSuggestion.metadata.processed

    private fun suggestSuggestions(userSuggestions: MutableList<UserSuggestion>) =
        Flowable.fromIterable(userSuggestions)
            .map { it.suggestion }
            .toList()
            .toObservable()
            .flatMap { suggestionServerSource.suggest(PostSuggestRequest(it)) }
            .flatMap { userSuggestionRepository.updateList(userSuggestions) }
}