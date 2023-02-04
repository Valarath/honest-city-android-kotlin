package cz.city.honest.repository.vote

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.Vote
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import cz.city.honest.repository.suggestion.SuggestionRepository
import cz.city.honest.repository.RepositoryProvider
import cz.city.honest.repository.toBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

//open class VoteRepository<SUGGESTION_TYPE : Suggestion>(
class VoteRepository(
    operationProvider: DatabaseOperationProvider,
// private val suggestionTypeClass: Class<SUGGESTION_TYPE>,
    private val suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) : Repository<Vote>(operationProvider) {

    override fun get(userIds: List<String>): Flowable<Vote> =
        findVotes(userIds)
            .flatMap { toEntities(it) { get(it) } }


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

    private fun updateSuggestion(entity: Vote) =
        RepositoryProvider.provide(suggestionRepositories, entity.suggestion.javaClass)
            ?.update(entity.suggestion)

    override fun update(entity: Vote): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "user_id = ? AND suggestion_id = ?",
                arrayOf(entity.userId, entity.suggestion.id)
            )
        )
            .flatMap { updateSuggestion(entity) }

    private fun getVoteUserSuggestions(
        suggestionId: String
    ): Flowable<Suggestion> = Flowable.fromIterable(suggestionRepositories.values)
        .flatMap {it.get(listOf(suggestionId))  }

//        Flowable.just(suggestionId)
//        .flatMap { getSuggestionTypeRepository().get(listOf(it)) }

//    protected fun getSuggestionTypeRepository(): SuggestionRepository<SUGGESTION_TYPE> =
//        suggestionRepositories[suggestionTypeClass.simpleName]
//            .run { this as SuggestionRepository<SUGGESTION_TYPE> }

    override fun delete(entity: Vote): Observable<Int> = Observable.just(
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


    private fun getContentValues(entity: Vote): ContentValues = ContentValues().apply {
        put("user_id", entity.userId)
        put("suggestion_id", entity.suggestion.id)
        put("processed", entity.processed)
    }

    companion object {
        val TABLE_NAME = "user_vote"
    }

}

