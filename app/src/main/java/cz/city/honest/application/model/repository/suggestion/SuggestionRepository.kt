package cz.city.honest.application.model.repository.suggestion

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.suggestion.exchange.ExchangeRateSuggestionRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class SuggestionRepository<SUGGESTION_TYPE : Suggestion>(databaseOperationProvider: DatabaseOperationProvider):
    Repository<SUGGESTION_TYPE>(databaseOperationProvider) {

    abstract fun get():Flowable<SUGGESTION_TYPE>

    override fun insert(suggestion: SUGGESTION_TYPE): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            getContentValues(suggestion),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    override fun update(suggestion: SUGGESTION_TYPE): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(suggestion),
            "id = ?",
            arrayOf(suggestion.id)
        )
    )

    override fun delete(suggestion: SUGGESTION_TYPE): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            ExchangeRateSuggestionRepository.TABLE_NAME,
            "id = ?",
            arrayOf(suggestion.id)
        )
    )

    private fun getContentValues(suggestion: Suggestion) = ContentValues().apply {
        put("id", suggestion.id)
        put("votes", suggestion.votes)
        put("status", suggestion.state.name)
    }

    companion object {
        const val TABLE_NAME: String = "suggestion"
    }

}





