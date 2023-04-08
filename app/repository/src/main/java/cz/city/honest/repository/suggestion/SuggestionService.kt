package cz.city.honest.repository.suggestion

import cz.city.honest.dto.Suggestion
import cz.city.honest.service.gateway.internal.InternalSuggestionGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SuggestionService(private val suggestionRepository: SuggestionRepository) :
    InternalSuggestionGateway {

    override fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
            suggestionRepository.getBySubjectId(id)
            .toObservable()

    override fun getUnvotedSuggestionsForSubject(id: String): Observable<Suggestion> =
        suggestionRepository.getUnvotedBySubjectId(id)
            .toObservable()

    override fun <SUGGESTION_TYPE : Suggestion> getSuggestions(clazz: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE> =
        suggestionRepository.getBySuggestionType(clazz)

    override fun suggest(suggestion: Suggestion): Observable<Unit> =
        suggestionRepository
            .insert(suggestion)
            .map { }
}