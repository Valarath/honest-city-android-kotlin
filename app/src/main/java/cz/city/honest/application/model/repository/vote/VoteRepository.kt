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

class VoteRepository(
    operationProvider: DatabaseOperationProvider,
    val suggestionRepositories: Map<Class<out Suggestion>, @JvmSuppressWildcards SuggestionRepository<out Suggestion>>
) :
    Repository<Vote>(operationProvider) {

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
                ExchangePointRepository.TABLE_NAME,
                getContentValues(entity),
                "where user_id = ? && suggestion_id = ?",
                arrayOf(entity.userId, entity.suggestion.id.toString())
            )
        )
            .flatMap { updateSuggestion(entity) }

    override fun get(id: List<Long>): Flowable<Vote> {
        TODO("Not yet implemented")
    }

    private fun findVotes(subjectIds: List<Long>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select user_id, suggestion_id from user_vote where user_id in( ${
                    mapToQueryParamSymbols(
                        subjectIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(subjectIds))
            )
        )

    override fun delete(entity: Vote): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            SubjectRepository.TABLE_NAME,
            "where user_id = ? && suggestion_id = ?",
            arrayOf(entity.userId, entity.suggestion.id.toString())
        )
    )
        .flatMap {updateSuggestion(entity)  }

    private fun getContentValues(entity: Vote): ContentValues = ContentValues().apply {
        put("user_id", entity.userId)
        put("suggestion_id", entity.suggestion.id)
    }


    companion object {
        val TABLE_NAME = "user_vote"
    }

}