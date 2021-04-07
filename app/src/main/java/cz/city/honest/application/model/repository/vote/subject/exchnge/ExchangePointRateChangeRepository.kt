package cz.city.honest.application.model.repository.vote.subject.exchnge

import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.mobile.model.dto.VoteForExchangePointRateChange
import io.reactivex.rxjava3.core.Flowable

class ExchangePointRateChangeRepository (
    operationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : VoteRepository<VoteForExchangePointRateChange, ExchangeRateSuggestion>(
    operationProvider, ExchangeRateSuggestion::class.java, suggestionRepositories
) {
    override fun get(userIds: List<String>): Flowable<VoteForExchangePointRateChange> =
        getVoteUserSuggestions(userIds)
            .map { VoteForExchangePointRateChange(it,getUserId(userIds)) }
}