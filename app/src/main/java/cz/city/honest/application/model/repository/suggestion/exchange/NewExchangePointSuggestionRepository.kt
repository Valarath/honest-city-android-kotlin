package cz.city.honest.application.model.repository.suggestion.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.toBoolean
import cz.city.honest.mobile.model.dto.Position
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class NewExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider) :
    SuggestionRepository<NewExchangePointSuggestion>(
        databaseOperationProvider
    ) {

    override fun insert(suggestion: NewExchangePointSuggestion): Observable<Long> =
        super.insert(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.insertWithOnConflict(
                    TABLE_NAME,
                    null,
                    getContentValues(suggestion),
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }

    override fun update(suggestion: NewExchangePointSuggestion): Observable<Int> =
        super.update(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(suggestion),
                    "where id = ?",
                    arrayOf(suggestion.id.toString())
                )
            }

    override fun get(id: List<String>): Flowable<NewExchangePointSuggestion> =
        findClosedExchangePointSuggestions(id).flatMap {
            toEntities(it) {
                toNewExchangePointSuggestion(it)
            }
        }

    override fun delete(suggestion: NewExchangePointSuggestion): Observable<Int> =
        super.delete(suggestion).map {
            databaseOperationProvider.writableDatabase.delete(
                ExchangeRateSuggestionRepository.TABLE_NAME,
                "where id = ?",
                arrayOf(suggestion.id.toString())
            )
        }

    private fun findClosedExchangePointSuggestions(subjectId: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, state, votes, longitude, latitude, suggestion_id from new_exchange_point_suggestion join suggestion on new_exchange_point_suggestion.suggestion_id = suggestion.id where suggestion_id in( ${mapToQueryParamSymbols(subjectId)})",
                arrayOf(mapToQueryParamVariable(subjectId))
            )
        )

    private fun toNewExchangePointSuggestion(cursor: Cursor): Flowable<NewExchangePointSuggestion> =
        Flowable.just(
            NewExchangePointSuggestion(
                id = cursor.getString(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                position = Position(cursor.getDouble(3), cursor.getDouble(4)),
                suggestionId = cursor.getString(5)
            )
        )

    private fun getContentValues(suggestion: NewExchangePointSuggestion) =
        ContentValues().apply {
            put("suggestion_id", suggestion.id)
            put("latitude", suggestion.position.latitude)
            put("longitude", suggestion.position.longitude)
        }

    companion object {
        const val TABLE_NAME: String = "new_exchange_point_suggestion"
    }
}