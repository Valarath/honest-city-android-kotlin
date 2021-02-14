package cz.city.honest.application.model.repository.suggestion

import android.content.ContentValues
import android.database.Cursor
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.ExchangeRateRepository
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Mono

class ExchangeRateSuggestionRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val exchangeRateRepository: ExchangeRateRepository
) :
    SuggestionRepository<ExchangeRateSuggestion>(databaseOperationProvider) {

    override fun insert(suggestion: ExchangeRateSuggestion): Observable<Long> =
        insertBaseSuggestion(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.insert(
                    TABLE_NAME,
                    null,
                    getContentValues(suggestion)
                )
            }

    override fun update(suggestion: ExchangeRateSuggestion): Observable<Int> =
        updateBaseSuggestion(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(suggestion),
                    "where id = ?",
                    arrayOf(suggestion.id.toString())
                )
            }

    override fun get(id: Long): Flowable<ExchangeRateSuggestion> =
        findExchangeRateSuggestions(id).flatMap {
            toEntities(it) {
                toExchangeRateSuggestion(it)
            }
        }

    override fun delete(id: List<Long>): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "where id = ?",
            arrayOf(id.toString())
        )
    )

    private fun findExchangeRateSuggestions(subjectId: Long): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT id, state, votes,exchange_rates_id, exchange_point_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.suggestion.id where exchange_point_id = ?",
                arrayOf(subjectId.toString())
            )
        )

    private fun toExchangeRateSuggestion(cursor: Cursor): Flowable<ExchangeRateSuggestion> =
        Flowable.fromObservable(
            exchangeRateRepository.getExchangeRates(cursor.getLong(3)),
            BackpressureStrategy.LATEST
        )
            .map {
                ExchangeRateSuggestion(
                    id = cursor.getLong(0),
                    state = State.valueOf(cursor.getString(1)),
                    votes = cursor.getInt(2),
                    suggestedExchangeRate = it,
                    exchangePointId = cursor.getLong(4)
                )
            }

    private fun getContentValues(suggestion: ExchangeRateSuggestion) =
        ContentValues().apply {
            put("suggestion_id", suggestion.id)
            put("exchange_point_id", suggestion.exchangePointId)
            put("exchange_rates_id", suggestion.suggestedExchangeRate.id)
        }

    companion object {
        const val TABLE_NAME: String = "exchange_rate_change_suggestion"
    }
}