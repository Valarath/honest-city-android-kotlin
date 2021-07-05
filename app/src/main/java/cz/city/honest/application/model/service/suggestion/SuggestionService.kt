package cz.city.honest.application.model.service.suggestion

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.service.RepositoryProvider
import cz.city.honest.application.model.service.user.UserSuggestionService
import cz.city.honest.application.model.service.vote.VoteService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.util.*

class SuggestionService(
    val suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>,
    val userSuggestionService: UserSuggestionService,
    val voteService: VoteService
) {

    fun createSuggestion(suggestion: Suggestion, markAs: UserSuggestionStateMarking) =
        userSuggestionService.suggest(suggestion, markAs)
            .flatMap { voteService.vote(suggestion) }

    fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
        Flowable.fromIterable(suggestionRepositories.values)
            .flatMap { it.get(listOf(id)) }
            .toObservable()


    fun <SUGGESTION_TYPE : Suggestion> getSuggestions(clazz: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE> =
        (RepositoryProvider.provide(
            suggestionRepositories,
            clazz
        ) as SuggestionRepository<SUGGESTION_TYPE>).get()

    fun suggest(suggestion: Suggestion) =
        RepositoryProvider.provide(
            suggestionRepositories,
            suggestion::class.java
        ).insert(suggestion)

    fun update(suggestion: Suggestion) =
        RepositoryProvider.provide(
            suggestionRepositories,
            suggestion::class.java
        )
            .update(updateSuggestion(suggestion))

    private fun updateSuggestion(suggestion: Suggestion) = suggestion.apply { this.increaseVotes() }

}