package cz.city.honest.application.model.repository.suggestion.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
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

    override fun getBySubjectId(id: String): Flowable<ClosedExchangePointSuggestion> =
        findClosedExchangePointSuggestionsForWatchedSubjects(listOf(id))
            .flatMap { toEntities(it) { toCloseExchangePointSuggestion(it) } }

    override fun getUnvotedBySubjectId(id: String): Flowable<ClosedExchangePointSuggestion> =
        findUnvotedClosedExchangePointSuggestionsForWatchedSubjects(listOf(id))
            .flatMap { toEntities(it) { toCloseExchangePointSuggestion(it) } }

    override fun delete(suggestion: ClosedExchangePointSuggestion): Observable<Int> =
        super.delete(suggestion).map {
            databaseOperationProvider.writableDatabase.delete(
                TABLE_NAME,
                "id = ?",
                arrayOf(suggestion.id)
            )
        }

    private fun findClosedExchangePointSuggestions(): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, state, votes, watched_subject_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.id = suggestion.id",
                arrayOf()
            )
        )

    private fun findClosedExchangePointSuggestions(subjectId: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, status, votes, watched_subject_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.id = suggestion.id ${mapToQueryParamSymbols(subjectId,"where suggestion.id in")}",
                getMapParameterArray(subjectId)
            )
        )

    private fun findClosedExchangePointSuggestionsForWatchedSubjects(exchangePointIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, status, votes, watched_subject_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.id = suggestion.id where watched_subject_id in( ${mapToQueryParamSymbols(exchangePointIds)})",
                arrayOf(mapToQueryParamVariable(exchangePointIds))
            )
        )

    private fun findUnvotedClosedExchangePointSuggestionsForWatchedSubjects(exchangePointIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select closed_exchange_point_suggestion.id, status, votes, watched_subject_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.id = suggestion.id left join user_vote on suggestion.id = user_vote.suggestion_id where watched_subject_id in( ${mapToQueryParamSymbols(exchangePointIds)}) AND user_vote.suggestion_id IS NULL",
                arrayOf(mapToQueryParamVariable(exchangePointIds))
            )
        )

    private fun toCloseExchangePointSuggestion(cursor: Cursor): Flowable<ClosedExchangePointSuggestion> =
        Flowable.just(
            ClosedExchangePointSuggestion(
                id = cursor.getString(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                subjectId = cursor.getString(3)
            )
        )

    private fun getContentValues(suggestion: ClosedExchangePointSuggestion) =
        ContentValues().apply {
            put("id",suggestion.id)
            put("watched_subject_id", suggestion.subjectId)
        }

    companion object {
        const val TABLE_NAME: String = "closed_exchange_point_suggestion"
    }
}