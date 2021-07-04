package cz.city.honest.application.model.repository.vote.subject.exchnge

import android.database.Cursor
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.VoteForExchangePointDelete
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.toBoolean
import cz.city.honest.application.model.repository.vote.VoteRepository
import io.reactivex.rxjava3.core.Flowable

class ExchangePointDeleteVoteRepository(
    operationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : VoteRepository<VoteForExchangePointDelete, ClosedExchangePointSuggestion>(
    operationProvider, ClosedExchangePointSuggestion::class.java, suggestionRepositories
) {
    override fun get(userIds: List<String>): Flowable<VoteForExchangePointDelete> =
        findVotes(userIds)
            .flatMap { toEntities(it) { get(it) } }

    private fun get(cursor: Cursor) = getVoteUserSuggestions(cursor.getString(1))
        .map {
            VoteForExchangePointDelete(
                suggestion = it,
                userId = cursor.getString(0),
                processed = cursor.getInt(2).toBoolean()
            )
        }
}