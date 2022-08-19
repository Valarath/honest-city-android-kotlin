package cz.city.honest.service.suggestion

import cz.city.honest.service.RepositoryProvider
import cz.city.honest.service.user.UserSuggestionService
import cz.city.honest.service.vote.VoteService
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.UserSuggestionStateMarking
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SuggestionService(
    val suggestionRepositories: Map<String, @JvmSuppressWildcards cz.city.honest.repository.suggestion.SuggestionRepository<out Suggestion>>,
    val userSuggestionService: UserSuggestionService,
    val voteService: VoteService
) {

    fun createSuggestion(suggestion: Suggestion, markAs: UserSuggestionStateMarking) =
        userSuggestionService.suggest(suggestion, markAs)
            .flatMap { voteService.vote(suggestion) }

    fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
        Flowable.fromIterable(suggestionRepositories.values)
            .flatMap { it.getBySubjectId(id) }
            .toObservable()

    fun getUnvotedSuggestionsForSubject(id: String): Observable<Suggestion> =
        Flowable.fromIterable(suggestionRepositories.values)
            .flatMap { it.getUnvotedBySubjectId(id) }
            .toObservable()


    fun <SUGGESTION_TYPE : Suggestion> getSuggestions(clazz: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE> =
        (RepositoryProvider.provide(
            suggestionRepositories,
            clazz
        ) as cz.city.honest.repository.suggestion.SuggestionRepository<SUGGESTION_TYPE>).get()

    fun suggest(suggestion: Suggestion) =
        RepositoryProvider.provide(
            suggestionRepositories,
            suggestion::class.java
        ).insert(suggestion)


}