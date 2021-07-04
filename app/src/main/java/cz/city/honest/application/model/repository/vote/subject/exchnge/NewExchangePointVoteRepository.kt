package cz.city.honest.application.model.repository.vote.subject.exchnge

import android.database.Cursor
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.VoteForExchangePointRateChange
import cz.city.honest.application.model.dto.VoteForNewExchangePoint
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.toBoolean
import cz.city.honest.application.model.repository.vote.VoteRepository
import io.reactivex.rxjava3.core.Flowable

class NewExchangePointVoteRepository(
    operationProvider: DatabaseOperationProvider,
    suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : VoteRepository<VoteForNewExchangePoint, NewExchangePointSuggestion>(
    operationProvider, NewExchangePointSuggestion::class.java, suggestionRepositories
) {

    override fun get(userIds: List<String>): Flowable<VoteForNewExchangePoint> =
        findVotes(userIds)
            .flatMap { toEntities(it) { get(it) } }


    private fun get(cursor: Cursor) = getVoteUserSuggestions(cursor.getString(1))
        .map {
            VoteForNewExchangePoint(
                suggestion = it,
                userId = cursor.getString(0),
                processed = cursor.getInt(2).toBoolean()
            )
        }

    private fun findVotes(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select user_id, suggestion_id, processed from user_vote join new_exchange_point_suggestion on suggestion_id = id where user_id in( ${
                    mapToQueryParamSymbols(
                        userIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(userIds))
            )
        )
}