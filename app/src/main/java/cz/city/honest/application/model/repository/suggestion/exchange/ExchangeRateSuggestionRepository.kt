package cz.city.honest.application.model.repository.suggestion.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.subject.exchange.ExchangeRateRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
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
                    "id = ?",
                    arrayOf(suggestion.id)
                )
            }

    override fun get(): Flowable<ExchangeRateSuggestion> =
        findExchangeRateSuggestions().flatMap {
            toEntities(it) {
                toExchangeRateSuggestion(it)
            }
        }

    override fun get(id: List<String>): Flowable<ExchangeRateSuggestion> =
        findExchangeRateSuggestions(id).flatMap {
            toEntities(it) {
                toExchangeRateSuggestion(it)
            }
        }

    fun getForWatchedSubjects(id: List<String>): Flowable<ExchangeRateSuggestion> =
        findExchangeRateSuggestionsForWatchedSubjects(id)
            .flatMap { toEntities(it) { toExchangeRateSuggestion(it) } }

    override fun delete(suggestion: ExchangeRateSuggestion): Observable<Int> =
        super.delete(suggestion).map {
            databaseOperationProvider.writableDatabase.delete(
                TABLE_NAME,
                "id = ?",
                arrayOf(suggestion.id)
            )
        }

    private fun findExchangeRateSuggestions(): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT exchange_rate_change_suggestion.id, state, votes,exchange_rates_id, watched_subject_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.id",
                arrayOf()
            )
        )

    private fun findExchangeRateSuggestions(subjectId: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT exchange_rate_change_suggestion.id, status, votes,exchange_rates_id, watched_subject_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.id where suggestion.id in( ${mapToQueryParamSymbols(subjectId)})",
                getMapParameterArray(subjectId)
            )
        )

    private fun findExchangeRateSuggestionsForWatchedSubjects(ids: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "SELECT exchange_rate_change_suggestion.id, status, votes,exchange_rates_id, watched_subject_id from exchange_rate_change_suggestion join suggestion on suggestion.id = exchange_rate_change_suggestion.id where watched_subject_id in( ${mapToQueryParamSymbols(ids)})",
                arrayOf(mapToQueryParamVariable(ids))
            )
        )

    private fun toExchangeRateSuggestion(cursor: Cursor): Flowable<ExchangeRateSuggestion> =
            exchangeRateRepository.get(listOf(cursor.getString(3)))
            .map {
                ExchangeRateSuggestion(
                    id = cursor.getString(0),
                    state = State.valueOf(cursor.getString(1)),
                    votes = cursor.getInt(2),
                    suggestedExchangeRate = it,
                    watchedSubjectId = cursor.getString(4)
                )
            }

    private fun getContentValues(suggestion: ExchangeRateSuggestion) =
        ContentValues().apply {
            put("id",suggestion.id)
            put("exchange_point_id", suggestion.watchedSubjectId)
            put("exchange_rates_id", suggestion.suggestedExchangeRate.id)
        }

    companion object {
        const val TABLE_NAME: String = "exchange_rate_change_suggestion"
    }
}