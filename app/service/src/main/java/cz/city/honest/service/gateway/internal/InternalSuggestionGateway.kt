package cz.city.honest.service.gateway.internal

import cz.city.honest.dto.Suggestion
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InternalSuggestionGateway {

    fun getSuggestionsForSubject(id: String): Observable<Suggestion>

    fun getUnvotedSuggestionsForSubject(id: String): Observable<Suggestion>

    fun <SUGGESTION_TYPE : Suggestion> getSuggestions(clazz: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE>

    fun suggest(suggestion: Suggestion):Observable<Unit>
}