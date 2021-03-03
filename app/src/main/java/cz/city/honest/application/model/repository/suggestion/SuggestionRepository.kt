package cz.city.honest.application.model.repository.suggestion

import android.content.ContentValues
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.suggestion.exchange.ExchangeRateSuggestionRepository
import io.reactivex.rxjava3.core.Observable

abstract class SuggestionRepository<SUGGESTION_TYPE : Suggestion>(databaseOperationProvider: DatabaseOperationProvider):
    Repository<SUGGESTION_TYPE>(databaseOperationProvider) {

    override fun insert(suggestion: SUGGESTION_TYPE): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insert(
            TABLE_NAME,
            null,
            getContentValues(suggestion)
        )
    )

    override fun update(suggestion: SUGGESTION_TYPE): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(suggestion),
            "where id = ?",
            arrayOf(suggestion.id.toString())
        )
    )

    override fun delete(suggestion: SUGGESTION_TYPE): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            ExchangeRateSuggestionRepository.TABLE_NAME,
            "where id = ?",
            arrayOf(suggestion.id.toString())
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





