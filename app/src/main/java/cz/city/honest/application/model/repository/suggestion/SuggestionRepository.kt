package cz.city.honest.application.model.repository.suggestion

import android.content.ContentValues
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class SuggestionRepository<SUGGESTION_TYPE : Suggestion>(databaseOperationProvider: DatabaseOperationProvider):
    Repository<SUGGESTION_TYPE>(databaseOperationProvider) {

    abstract fun insert(suggestion: SUGGESTION_TYPE): Observable<Long>
    abstract fun update(suggestion: SUGGESTION_TYPE): Observable<Int>
    abstract fun get(id:Long):Flowable<SUGGESTION_TYPE>
    abstract fun delete(id: List<Long>): Observable<Int>


    fun insertList(suggestions: List<SUGGESTION_TYPE>) =
        processListInTransaction(suggestions, ::insert)

    fun updateList(suggestions: List<SUGGESTION_TYPE>) =
        processListInTransaction(suggestions, ::update)

    protected fun insertBaseSuggestion(suggestion: Suggestion) = Observable.just(
        databaseOperationProvider.writableDatabase.insert(
            TABLE_NAME,
            null,
            getContentValues(suggestion)
        )
    )

    protected fun updateBaseSuggestion(suggestion: Suggestion) = Observable.just(
        databaseOperationProvider.writableDatabase.update(
            TABLE_NAME,
            getContentValues(suggestion),
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





