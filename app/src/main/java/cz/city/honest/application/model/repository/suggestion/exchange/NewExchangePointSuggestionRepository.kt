package cz.city.honest.application.model.repository.suggestion.exchange

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Position
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

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
                    "id = ?",
                    arrayOf(suggestion.id)
                )
            }

    override fun get(): Flowable<NewExchangePointSuggestion> =
        findNewExchangePointSuggestions().flatMap {
            toEntities(it) { toNewExchangePointSuggestion(it) }
        }


    override fun get(id: List<String>): Flowable<NewExchangePointSuggestion> =
        findNewExchangePointSuggestions(id).flatMap {
            toEntities(it) { toNewExchangePointSuggestion(it) }
        }

    override fun getBySubjectId(id: String): Flowable<NewExchangePointSuggestion> =
        findNewExchangePointSuggestions(id).flatMap {
            toEntities(it) {
                toNewExchangePointSuggestion(it)
            }
        }

    override fun getUnvotedBySubjectId(id: String): Flowable<NewExchangePointSuggestion> =
        findUnvotedNewExchangePointSuggestions(id).flatMap {
            toEntities(it) {
                toNewExchangePointSuggestion(it)
            }
        }

    override fun delete(suggestion: NewExchangePointSuggestion): Observable<Int> =
        super.delete(suggestion).map {
            databaseOperationProvider.writableDatabase.delete(
                TABLE_NAME,
                "id = ?",
                arrayOf(suggestion.id)
            )
        }

    private fun findNewExchangePointSuggestions(): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select new_exchange_point_suggestion.id, status, votes, longitude, latitude, exchange_point_id from new_exchange_point_suggestion join suggestion on new_exchange_point_suggestion.id = suggestion.id where exchange_point_id is null group by longitude, latitude",
                arrayOf()
            )
        )


    private fun findNewExchangePointSuggestions(suggestionsId: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select new_exchange_point_suggestion.id, status, votes, longitude, latitude, exchange_point_id from new_exchange_point_suggestion join suggestion on new_exchange_point_suggestion.id = suggestion.id where suggestion.id in( ${
                    mapToQueryParamSymbols(
                        suggestionsId
                    )
                })",
                getMapParameterArray(suggestionsId)
            )
        )

    private fun findNewExchangePointSuggestions(subjectId: String): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select new_exchange_point_suggestion.id, status, votes, longitude, latitude, exchange_point_id from new_exchange_point_suggestion join suggestion on new_exchange_point_suggestion.id = suggestion.id where exchange_point_id = ?",
                getMapParameterArray(listOf(subjectId))
            )
        )

    private fun findUnvotedNewExchangePointSuggestions(subjectId: String): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select new_exchange_point_suggestion.id, status, votes, longitude, latitude, exchange_point_id from new_exchange_point_suggestion join suggestion on new_exchange_point_suggestion.id = suggestion.id left join user_vote on suggestion.id = user_vote.suggestion_id where exchange_point_id = ? AND user_vote.suggestion_id IS NULL",
                getMapParameterArray(listOf(subjectId))
            )
        )

    private fun toNewExchangePointSuggestion(cursor: Cursor): Flowable<NewExchangePointSuggestion> =
        Flowable.just(
            NewExchangePointSuggestion(
                id = cursor.getString(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                position = Position(cursor.getDouble(3), cursor.getDouble(4)),
                subjectId = cursor.getString(5)
            )
        )

    private fun getContentValues(suggestion: NewExchangePointSuggestion) =
        ContentValues().apply {
            put("id", suggestion.id)
            put("latitude", suggestion.position.latitude)
            put("longitude", suggestion.position.longitude)
            put("exchange_point_id", suggestion.subjectId)
        }


    companion object {
        const val TABLE_NAME: String = "new_exchange_point_suggestion"
    }
}