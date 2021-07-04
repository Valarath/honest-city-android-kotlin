package cz.city.honest.application.model.repository.vote

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.Vote
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.service.RepositoryProvider
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class VoteRepository<VOTE_ENTITY : Vote, SUGGESTION_TYPE : Suggestion>(
    operationProvider: DatabaseOperationProvider,
    private val suggestionTypeClass: Class<SUGGESTION_TYPE>,
    private val suggestionRepositories: Map<String, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) :
    Repository<VOTE_ENTITY>(operationProvider) {

    fun getBySubjectId(subjectId:String, userId:String):Flowable<VOTE_ENTITY> =
        get(listOf(userId))
            .filter { it.suggestion.id == subjectId }

    override fun insert(entity: VOTE_ENTITY): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )
        .flatMap { updateSuggestion(entity) }
        .map { 0L }

    private fun updateSuggestion(entity: VOTE_ENTITY) =
        RepositoryProvider.provide(suggestionRepositories, entity.suggestion.javaClass)
            ?.update(entity.suggestion)

    override fun update(entity: VOTE_ENTITY): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "user_id = ? && suggestion_id = ?",
                arrayOf(entity.userId, entity.suggestion.id)
            )
        )
            .flatMap { updateSuggestion(entity) }

    protected fun getVoteUserSuggestions(
        suggestionId:String
    ): Flowable<SUGGESTION_TYPE> = Flowable.just(suggestionId)
        .flatMap { getSuggestionTypeRepository().get(listOf(it)) }

    protected fun getSuggestionTypeRepository(): SuggestionRepository<SUGGESTION_TYPE> =
        suggestionRepositories[suggestionTypeClass.simpleName]
            .run {this as SuggestionRepository<SUGGESTION_TYPE> }

    protected fun findVotes(userIds: List<String>): Flowable<Cursor> =
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

    protected fun getAllSuggestionIds(cursor: Cursor): List<String> =
        mutableListOf<String>()
            .apply {
                while (cursorContainsData(cursor))
                    add(cursor.getString(1))
            }
            .run { this.toList() }

    override fun delete(entity: VOTE_ENTITY): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "user_id = ? && suggestion_id = ?",
            arrayOf(entity.userId, entity.suggestion.id)
        )
    )
        .flatMap { updateSuggestion(entity) }

    fun delete(suggestionId:String,userId:String): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "user_id = ? && suggestion_id = ?",
            arrayOf(userId, suggestionId)
        )
    )

    private fun getContentValues(entity: VOTE_ENTITY): ContentValues = ContentValues().apply {
        put("user_id", entity.userId)
        put("suggestion_id", entity.suggestion.id)
        put("processed",entity.processed)
    }


    companion object {
        val TABLE_NAME = "user_vote"
    }

}

