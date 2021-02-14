package cz.city.honest.application.model.repository.suggestion

import android.content.ContentValues
import android.database.Cursor
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.mobile.model.dto.Position
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class NewExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider) :
    SuggestionRepository<NewExchangePointSuggestion>(
        databaseOperationProvider
    ) {

    override fun insert(suggestion: NewExchangePointSuggestion): Observable<Long> =
        insertBaseSuggestion(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.insert(
                    TABLE_NAME,
                    null,
                    getContentValues(suggestion)
                )
            }

    override fun update(suggestion: NewExchangePointSuggestion): Observable<Int> =
        updateBaseSuggestion(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(suggestion),
                    "where id = ?",
                    arrayOf(suggestion.id.toString())
                )
            }

    override fun get(id: Long): Flowable<NewExchangePointSuggestion> =
        findClosedExchangePointSuggestions(id).flatMap {
            toEntities(it) {
                toNewExchangePointSuggestion(it)
            }
        }

    override fun delete(id: List<Long>): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            ExchangeRateSuggestionRepository.TABLE_NAME,
            "where id = ?",
            arrayOf(id.toString())
        )
    )

    private fun findClosedExchangePointSuggestions(subjectId: Long): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, state, votes, longitude, latitude from new_exchange_point_suggestion join suggestion on new_exchange_point_suggestion.suggestion_id = suggestion.id where exchange_point_id = ?",
                arrayOf(subjectId.toString())
            )
        )

    private fun toNewExchangePointSuggestion(cursor: Cursor): Flowable<NewExchangePointSuggestion> =
        Flowable.just(
            NewExchangePointSuggestion(
                id = cursor.getLong(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                position = Position(cursor.getDouble(3), cursor.getDouble(4))
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