package cz.city.honest.application.model.repository.suggestion.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.toBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class ClosedExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider) :
    SuggestionRepository<ClosedExchangePointSuggestion>(databaseOperationProvider) {

    override fun insert(suggestion: ClosedExchangePointSuggestion): Observable<Long> =
        super.insert(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.insertWithOnConflict(
                    TABLE_NAME,
                    "",
                    getContentValues(suggestion),
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }

    override fun update(suggestion: ClosedExchangePointSuggestion): Observable<Int> =
        super.update(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(suggestion),
                    "id = ?",
                    arrayOf(suggestion.id)
                )
            }

    override fun get(): Flowable<ClosedExchangePointSuggestion> =
        findClosedExchangePointSuggestions()
            .flatMap { toEntities(it) { toCloseExchangePointSuggestion(it) } }

    override fun get(id: List<String>): Flowable<ClosedExchangePointSuggestion> =
        findClosedExchangePointSuggestions(id)
            .flatMap { toEntities(it) { toCloseExchangePointSuggestion(it) } }

    fun getForWatchedSubjects(id: List<String>): Flowable<ClosedExchangePointSuggestion> =
        findClosedExchangePointSuggestionsForWatchedSubjects(id)
            .flatMap { toEntities(it) { toCloseExchangePointSuggestion(it) } }

    override fun delete(suggestion: ClosedExchangePointSuggestion): Observable<Int> =
        super.delete(suggestion).map {
            databaseOperationProvider.writableDatabase.delete(
                ExchangeRateSuggestionRepository.TABLE_NAME,
                "id = ?",
                arrayOf(suggestion.id)
            )
        }

    private fun findClosedExchangePointSuggestions(): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, state, votes, watched_subject_id, suggestion_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.suggestion_id = suggestion.id",
                arrayOf()
            )
        )

    private fun findClosedExchangePointSuggestions(subjectId: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, status, votes, watched_subject_id, suggestion_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.suggestion_id = suggestion.id where suggestion_id in( ${mapToQueryParamSymbols(subjectId)})",
                arrayOf(mapToQueryParamVariable(subjectId))
            )
        )

    private fun findClosedExchangePointSuggestionsForWatchedSubjects(exchangePointIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, status, votes, exchange_point_id, suggestion_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.suggestion_id = suggestion.id where exchange_point_id in( ${mapToQueryParamSymbols(exchangePointIds)})",
                arrayOf(mapToQueryParamVariable(exchangePointIds))
            )
        )

    private fun toCloseExchangePointSuggestion(cursor: Cursor): Flowable<ClosedExchangePointSuggestion> =
        Flowable.just(
            ClosedExchangePointSuggestion(
                id = cursor.getString(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                watchedSubjectId = cursor.getString(3),
                suggestionId = cursor.getString(4)
            )
        )

    private fun getContentValues(suggestion: ClosedExchangePointSuggestion) =
        ContentValues().apply {
            put("id",suggestion.id)
            put("suggestion_id", suggestion.suggestionId)
            put("watched_subject_id", suggestion.watchedSubjectId)
        }

    companion object {
        const val TABLE_NAME: String = "closed_exchange_point_suggestion"
    }
}