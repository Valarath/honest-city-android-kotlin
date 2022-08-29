package cz.city.honest.service.suggestion

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.UserSuggestionStateMarking
import cz.city.honest.service.gateway.internal.InternalSuggestionGateway
import cz.city.honest.service.user.UserSuggestionService
import cz.city.honest.service.vote.VoteService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SuggestionService(
    private val internalSuggestionGateway: InternalSuggestionGateway,
    private val userSuggestionService: UserSuggestionService,
    private val voteService: VoteService
) {

    fun createSuggestion(suggestion: Suggestion, markAs: UserSuggestionStateMarking) =
        userSuggestionService.suggest(suggestion, markAs)
            .flatMap { voteService.vote(suggestion) }

    fun getSuggestionsForSubject(id: String): Observable<Suggestion> =
        internalSuggestionGateway.getSuggestionsForSubject(id)

    fun getUnvotedSuggestionsForSubject(id: String): Observable<Suggestion> =
        internalSuggestionGateway.getUnvotedSuggestionsForSubject(id)

    fun <SUGGESTION_TYPE : Suggestion> getSuggestions(clazz: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE> =
        internalSuggestionGateway.getSuggestions(clazz)

    fun suggest(suggestion: Suggestion) =
        internalSuggestionGateway.suggest(suggestion)

}