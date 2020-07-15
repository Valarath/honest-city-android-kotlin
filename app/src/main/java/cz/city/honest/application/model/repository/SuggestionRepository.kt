package cz.city.honest.application.model.repository

import android.database.Cursor
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.SubjectGateway
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuggestionRepository @Inject constructor(
    val databaseOperationProvider: DatabaseOperationProvider,
    val exchangeRateRepository: ExchangeRateRepository
) : SubjectGateway {


    fun getSubjectSuggestions(subjectId: Long): Mono<MutableList<Suggestion>> =
        Mono.zip(
            getSubjectExchangeRateSuggestions(subjectId),
            getSubjectCloseExchangePointSuggestions(subjectId)
        )
            .map { concatSuggestions(it) }

    private fun concatSuggestions(it: Tuple2<MutableList<Suggestion>, MutableList<Suggestion>>) =
        mutableListOf<Suggestion>()
            .apply {
                addAll(it.t1)
                addAll(it.t2)
            }

    private fun getSubjectExchangeRateSuggestions(subjectId: Long): Mono<MutableList<Suggestion>> =
        Mono.just(findExchangeRateSuggestions(subjectId))
            .flatMap { toSuggestions(it, { toExchangeRateSuggestion(it) }).apply { it.close() } }

    private fun findExchangeRateSuggestions(subjectId: Long): Cursor =
        databaseOperationProvider.readableDatabase.rawQuery(
            "SELECT id, state, votes,exchange_rates_id, exchange_point_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.suggestion.id where exchange_point_id = $subjectId",
            null
        )


    private fun toSuggestions(
        cursor: Cursor, toSuggestion: (cursor: Cursor) -> Mono<out Suggestion>
    ): Mono<MutableList<Suggestion>> = Mono.just(mutableListOf<Suggestion>())
        .repeat { cursor.moveToNext() }
        .flatMap { toSuggestion(cursor) }
        .collectList()


    private fun toExchangeRateSuggestion(cursor: Cursor): Mono<ExchangeRateSuggestion> =
        exchangeRateRepository.getExchangeRates(cursor.getLong(3))
            .map {
                ExchangeRateSuggestion(
                    id = cursor.getLong(0),
                    state = State.valueOf(cursor.getString(1)),
                    votes = cursor.getInt(2),
                    suggestedExchangeRate = it,
                    exchangePointId = cursor.getLong(4)
                )
            }

    private fun getSubjectCloseExchangePointSuggestions(subjectId: Long): Mono<MutableList<Suggestion>> =
        Mono.just(findClosedExchangePointSuggestions(subjectId))
            .flatMap {
                toSuggestions(it, { toCloseExchangePointSuggestion(it) }).apply { it.close() }
            }

    private fun findClosedExchangePointSuggestions(subjectId: Long): Cursor =
        databaseOperationProvider.readableDatabase.rawQuery(
            "Select id, state, votes, exchange_point_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.suggestion_id = suggestion.id where exchange_point_id = $subjectId",
            null
        )


    private fun toCloseExchangePointSuggestion(cursor: Cursor): Mono<ClosedExchangePointSuggestion> =
        Mono.just(
            ClosedExchangePointSuggestion(
                id = cursor.getLong(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                exchangePointId = cursor.getLong(3)
            )
        )

}