package cz.city.honest.repository.vote.subject.exchnge

import android.database.Cursor
import cz.city.honest.dto.ExchangeRateSuggestion
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.VoteForExchangePointRateChange
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.repository.toBoolean
import cz.city.honest.repository.vote.VoteRepository
import io.reactivex.rxjava3.core.Flowable

class ExchangePointRateChangeVoteRepository (
    operationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : VoteRepository<VoteForExchangePointRateChange, ExchangeRateSuggestion>(
    operationProvider, ExchangeRateSuggestion::class.java, suggestionRepositories
) {
    override fun get(userIds: List<String>): Flowable<VoteForExchangePointRateChange> =
        findVotes(userIds)
            .flatMap { toEntities(it) { get(it) } }

    private fun get(cursor: Cursor) = getVoteUserSuggestions(cursor.getString(1))
        .map {
            VoteForExchangePointRateChange(
                suggestion = it,
                userId = cursor.getString(0),
                processed = cursor.getInt(2).toBoolean()
            )
        }

    private fun findVotes(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select user_id, suggestion_id, processed from user_vote join exchange_rate_change_suggestion on suggestion_id = id where user_id in( ${
                    mapToQueryParamSymbols(
                        userIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(userIds))
            )
        )
}