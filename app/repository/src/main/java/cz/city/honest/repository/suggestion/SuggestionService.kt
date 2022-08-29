package cz.city.honest.repository.suggestion

import cz.city.honest.dto.Suggestion
import cz.city.honest.repository.RepositoryProvider
import cz.city.honest.service.gateway.internal.InternalSuggestionGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SuggestionService(private val suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>) :
    InternalSuggestionGateway {

    override fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
        Flowable.fromIterable(suggestionRepositories.values)
            .flatMap { it.getBySubjectId(id) }
            .toObservable()

    override fun getUnvotedSuggestionsForSubject(id: String): Observable<Suggestion> =
        Flowable.fromIterable(suggestionRepositories.values)
            .flatMap { it.getUnvotedBySubjectId(id) }
            .toObservable()

    override fun <SUGGESTION_TYPE : Suggestion> getSuggestions(clazz: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE> =
        (RepositoryProvider.provide(
            suggestionRepositories,
            clazz
        ) as SuggestionRepository<SUGGESTION_TYPE>).get()

    override fun suggest(suggestion: Suggestion): Observable<Unit> =
        RepositoryProvider.provide(
            suggestionRepositories,
            suggestion::class.java
        )
            .insert(suggestion)
            .map { }
}