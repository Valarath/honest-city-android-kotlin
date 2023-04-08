package cz.city.honest.repository.suggestion

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.Suggestion
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class SuggestionRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val objectMapper: ObjectMapper
) : Repository<Suggestion>(databaseOperationProvider) {

    fun get(id: List<String>): Flowable<Suggestion> = findSuggestions(id)
        .flatMap {
            toEntities(it) {
                toSuggestion(it)
            }
        }

    fun <SUGGESTION_TYPE : Suggestion> getBySuggestionType(suggestionType: Class<SUGGESTION_TYPE>): Flowable<out SUGGESTION_TYPE> =
        findSuggestionsByType(suggestionType)
            .flatMap {
                toEntities(it) {
                    toSuggestion(it)
                }
            }
            .ofType(suggestionType)

    fun getBySubjectId(id: String): Flowable<Suggestion> = findSuggestions(id)
        .flatMap {
            toEntities(it) {
                toSuggestion(it)
            }
        }

    fun getUnvotedBySubjectId(id: String): Flowable<Suggestion> = findUnvotedSuggestions(id)
        .flatMap {
            toEntities(it) {
                toSuggestion(it)
            }
        }

    override fun insert(suggestion: Suggestion): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            getContentValues(suggestion),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    fun update(suggestion: Suggestion): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(suggestion),
                "id = ?",
                arrayOf(suggestion.id)
            )
        )

    fun delete(suggestion: Suggestion): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "id = ?",
            arrayOf(suggestion.id)
        )
    )

    private fun getContentValues(suggestion: Suggestion) = ContentValues().apply {
        put("id", suggestion.id)
        put("subject_id", suggestion.subjectId)
        put("class", suggestion.javaClass.name)
        put("data", objectMapper.writeValueAsString(suggestion))
    }

    private fun toSuggestion(cursor: Cursor): Flowable<Suggestion> = Flowable.just(
        objectMapper.readValue(cursor.getString(3), getClassForName(cursor.getString(2)))
    )

    private fun findSuggestions(suggestionsIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, subject_id, class, data from suggestion where suggestion.id in( ${
                    mapToQueryParamSymbols(
                        suggestionsIds
                    )
                })",
                getMapParameterArray(suggestionsIds)
            )
        )

    private fun findSuggestions(subjectId: String): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, subject_id, class, data from suggestion where subject_id = ?",
                getMapParameterArray(listOf(subjectId))
            )
        )

    private fun findUnvotedSuggestions(subjectId: String): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, subject_id, class, data from suggestion left join user_vote on suggestion.id = user_vote.suggestion_id where subject_id = ? AND user_vote.suggestion_id IS NULL",
                getMapParameterArray(listOf(subjectId))
            )
        )

    private fun <SUGGESTION_TYPE : Suggestion> findSuggestionsByType(suggestionType: Class<SUGGESTION_TYPE>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, subject_id, class, data from suggestion left join user_vote on suggestion.id = user_vote.suggestion_id where class = ?",
                getMapParameterArray(listOf(suggestionType.name))
            )
        )

    companion object {
        const val TABLE_NAME: String = "suggestion"
    }
}





