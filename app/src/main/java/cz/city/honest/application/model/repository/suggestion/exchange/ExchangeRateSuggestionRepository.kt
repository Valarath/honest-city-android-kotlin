package cz.city.honest.application.model.repository.suggestion.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.subject.exchange.ExchangeRateRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.toBoolean
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class ExchangeRateSuggestionRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val exchangeRateRepository: ExchangeRateRepository
) :
    SuggestionRepository<ExchangeRateSuggestion>(databaseOperationProvider) {

    override fun insert(suggestion: ExchangeRateSuggestion): Observable<Long> =
        super.insert(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.insertWithOnConflict(
                    TABLE_NAME,
                    null,
                    getContentValues(suggestion),
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }

    override fun update(suggestion: ExchangeRateSuggestion): Observable<Int> =
        super.update(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(suggestion),
                    "where id = ?",
                    arrayOf(suggestion.id.toString())
                )
            }

    override fun get(id: List<Long>): Flowable<ExchangeRateSuggestion> =
        findExchangeRateSuggestions(id).flatMap {
            toEntities(it) {
                toExchangeRateSuggestion(it)
            }
        }

    fun getForWatchedSubjects(id: List<Long>): Flowable<ExchangeRateSuggestion> =
        findExchangeRateSuggestionsForWatchedSubjects(id)
            .flatMap { toEntities(it) { toExchangeRateSuggestion(it) } }

    override fun delete(suggestion: ExchangeRateSuggestion): Observable<Int> =
        super.delete(suggestion).map {
            databaseOperationProvider.writableDatabase.delete(
                TABLE_NAME,
                "where id = ?",
                arrayOf(suggestion.id.toString())
            )
        }

    private fun findExchangeRateSuggestions(subjectId: List<Long>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT id, state, votes,exchange_rates_id, watched_subject_id, suggestion_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.suggestion.id where suggestion_id in( ${mapToQueryParamSymbols(subjectId)})",
                arrayOf(mapToQueryParamVariable(subjectId))
            )
        )

    private fun findExchangeRateSuggestionsForWatchedSubjects(ids: List<Long>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT id, state, votes,exchange_rates_id, watched_subject_id, suggestion_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.suggestion.id where watched_subject_id in( ${mapToQueryParamSymbols(ids)})",
                arrayOf(mapToQueryParamVariable(ids))
            )
        )

    private fun toExchangeRateSuggestion(cursor: Cursor): Flowable<ExchangeRateSuggestion> =
            exchangeRateRepository.get(listOf(cursor.getLong(3)))
            .map {
                ExchangeRateSuggestion(
                    id = cursor.getLong(0),
                    state = State.valueOf(cursor.getString(1)),
                    votes = cursor.getInt(2),
                    suggestedExchangeRate = it,
                    watchedSubjectId = cursor.getLong(4),
                    suggestionId = cursor.getLong(5)
                )
            }

    private fun getContentValues(suggestion: ExchangeRateSuggestion) =
        ContentValues().apply {
            put("suggestion_id", suggestion.id)
            put("exchange_point_id", suggestion.watchedSubjectId)
            put("exchange_rates_id", suggestion.suggestedExchangeRate.id)
        }

    companion object {
        const val TABLE_NAME: String = "exchange_rate_change_suggestion"
    }
}