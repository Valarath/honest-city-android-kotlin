package cz.city.honest.application.model.repository.vote.subject.exchnge

import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.mobile.model.dto.VoteForNewExchangePoint
import io.reactivex.rxjava3.core.Flowable

class NewExchangePointVoteRepository (
    operationProvider: DatabaseOperationProvider,
    suggestionTypeClass: Class<NewExchangePointSuggestion>,
    suggestionRepositories: Map<Class<out Suggestion>, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : VoteRepository<VoteForNewExchangePoint, NewExchangePointSuggestion>(
    operationProvider, suggestionTypeClass, suggestionRepositories
) {
    override fun get(userIds: List<String>): Flowable<VoteForNewExchangePoint> =
        getVoteUserSuggestions(userIds)
            .map { VoteForNewExchangePoint(it,getUserId(userIds)) }
}