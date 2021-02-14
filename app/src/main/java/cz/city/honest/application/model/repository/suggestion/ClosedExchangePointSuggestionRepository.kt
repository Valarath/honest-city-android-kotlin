import android.content.ContentValues
import android.database.Cursor
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.State
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import reactor.core.publisher.Mono
import java.nio.channels.FileLock

class ClosedExchangePointSuggestionRepository(databaseOperationProvider: DatabaseOperationProvider) :
    SuggestionRepository<ClosedExchangePointSuggestion>(databaseOperationProvider) {

    override fun insert(suggestion: ClosedExchangePointSuggestion): Observable<Long> =
        insertBaseSuggestion(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.insert(
                    TABLE_NAME,
                    "",
                    getContentValues(suggestion)
                )
            }

    override fun update(suggestion: ClosedExchangePointSuggestion): Observable<Int> =
        updateBaseSuggestion(suggestion)
            .map {
                databaseOperationProvider.writableDatabase.update(
                    TABLE_NAME,
                    getContentValues(suggestion),
                    "where id = ?",
                    arrayOf(suggestion.id.toString())
                )
            }

    override fun get(id: Long): Flowable<ClosedExchangePointSuggestion> =
        findClosedExchangePointSuggestions(id)
            .flatMap { toEntities(it) { toCloseExchangePointSuggestion(it) } }

    override fun delete(id: List<Long>): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "where id = ?",
            arrayOf(id.toString())
        )
    )

    private fun findClosedExchangePointSuggestions(subjectId: Long): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, state, votes, exchange_point_id from closed_exchange_point_suggestion join suggestion on closed_exchange_point_suggestion.suggestion_id = suggestion.id where exchange_point_id = ?",
                arrayOf(subjectId.toString())
            )
        )

    private fun toCloseExchangePointSuggestion(cursor: Cursor): Flowable<ClosedExchangePointSuggestion> =
        Flowable.just(
            ClosedExchangePointSuggestion(
                id = cursor.getLong(0),
                state = State.valueOf(cursor.getString(1)),
                votes = cursor.getInt(2),
                exchangePointId = cursor.getLong(3)
            )
        )

    private fun getContentValues(suggestion: ClosedExchangePointSuggestion) =
        ContentValues().apply {
            put("suggestion_id", suggestion.id)
            put("exchange_point_it", suggestion.exchangePointId)
        }

    companion object {
        const val TABLE_NAME: String = "closed_exchange_point_suggestion"
    }
}