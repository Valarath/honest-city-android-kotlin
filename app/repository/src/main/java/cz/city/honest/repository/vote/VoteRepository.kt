package cz.city.honest.repository.vote

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.Vote
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.repository.toBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class VoteRepository(
    operationProvider: DatabaseOperationProvider,
    private val suggestionRepository: SuggestionRepository
) : Repository<Vote>(operationProvider) {

    fun get(userIds: List<String>): Flowable<Vote> =
        findVotes(userIds)
            .flatMap { toEntities(it) { get(it) } }

    fun getBySubjectId(subjectId: String, userId: String): Flowable<Vote> =
        get(listOf(userId))
            .filter { it.suggestion.subjectId == subjectId }

    override fun insert(entity: Vote): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )
        .flatMap { updateSuggestion(entity) }
        .map { 0L }

    fun update(entity: Vote): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "user_id = ? AND suggestion_id = ?",
                arrayOf(entity.userId, entity.suggestion.id)
            )
        )
            .flatMap { updateSuggestion(entity) }

    fun delete(entity: Vote): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "user_id = ? AND suggestion_id = ?",
            arrayOf(entity.userId, entity.suggestion.id)
        )
    )
        .flatMap { updateSuggestion(entity) }

    fun delete(suggestionId: String, userId: String): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "user_id = ? AND  suggestion_id = ?",
            arrayOf(userId, suggestionId)
        )
    )

    private fun get(cursor: Cursor) = getVoteUserSuggestions(cursor.getString(1))
        .map {
            Vote(
                suggestion = it,
                userId = cursor.getString(0),
                processed = cursor.getInt(2).toBoolean()
            )
        }

    private fun findVotes(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select user_id, suggestion_id, processed from user_vote where user_id in( ${
                    mapToQueryParamSymbols(
                        userIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(userIds))
            )
        )

    private fun updateSuggestion(entity: Vote) =
        suggestionRepository.update(entity.suggestion)

    private fun getVoteUserSuggestions(
        suggestionId: String
    ): Flowable<Suggestion> = suggestionRepository.get(listOf(suggestionId))

    private fun getContentValues(entity: Vote): ContentValues = ContentValues().apply {
        put("user_id", entity.userId)
        put("suggestion_id", entity.suggestion.id)
        put("processed", entity.processed)
    }

    companion object {
        val TABLE_NAME = "user_vote"
    }

}

