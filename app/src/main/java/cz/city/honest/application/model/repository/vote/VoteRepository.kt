package cz.city.honest.application.model.repository.vote

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.subject.SubjectRepository
import cz.city.honest.application.model.repository.subject.exchange.ExchangePointRepository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.service.RepositoryProvider
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.Vote
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

abstract class VoteRepository<VOTE_ENTITY : Vote, SUGGESTION_TYPE : Suggestion>(
    operationProvider: DatabaseOperationProvider,
    val suggestionTypeClass: Class<SUGGESTION_TYPE>,
    val suggestionRepositories: Map<Class<out Suggestion>, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) :
    Repository<VOTE_ENTITY>(operationProvider) {

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
                ExchangePointRepository.TABLE_NAME,
                getContentValues(entity),
                "where user_id = ? && suggestion_id = ?",
                arrayOf(entity.userId, entity.suggestion.id.toString())
            )
        )
            .flatMap { updateSuggestion(entity) }

    protected fun getUserId(userIds: List<String>):String = userIds.first()

    protected fun getVoteUserSuggestions(
        userIds: List<String>
    ): Flowable<SUGGESTION_TYPE> = findVotes(userIds)
        .map { getAllSuggestionIds(it) }
        .concatMap { getSuggestionTypeRepository().get(it) }

    private fun getSuggestionTypeRepository(): SuggestionRepository<SUGGESTION_TYPE> =
        suggestionRepositories[suggestionTypeClass]
            .run {this as SuggestionRepository<SUGGESTION_TYPE> }

    private fun findVotes(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select user_id, suggestion_id from user_vote where user_id in( ${
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
                while (cursor.moveToNext())
                    add(cursor.getString(1))
            }
            .run { this.toList() }

    override fun delete(entity: VOTE_ENTITY): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            SubjectRepository.TABLE_NAME,
            "where user_id = ? && suggestion_id = ?",
            arrayOf(entity.userId, entity.suggestion.id.toString())
        )
    )
        .flatMap { updateSuggestion(entity) }

    private fun getContentValues(entity: VOTE_ENTITY): ContentValues = ContentValues().apply {
        put("user_id", entity.userId)
        put("suggestion_id", entity.suggestion.id)
    }


    companion object {
        val TABLE_NAME = "user_vote"
    }

}

