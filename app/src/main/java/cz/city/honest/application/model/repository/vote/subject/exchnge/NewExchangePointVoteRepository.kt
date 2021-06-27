package cz.city.honest.application.model.repository.vote.subject.exchnge

import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.VoteForNewExchangePoint
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import io.reactivex.rxjava3.core.Flowable

class NewExchangePointVoteRepository (
    operationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : VoteRepository<VoteForNewExchangePoint, NewExchangePointSuggestion>(
    operationProvider, NewExchangePointSuggestion::class.java, suggestionRepositories
) {
    override fun get(userIds: List<String>): Flowable<VoteForNewExchangePoint> =
        getVoteUserSuggestions(userIds)
            .map { VoteForNewExchangePoint(it,getUserId(userIds)) }
}