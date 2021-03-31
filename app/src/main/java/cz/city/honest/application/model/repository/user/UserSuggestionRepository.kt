package cz.city.honest.application.model.repository.user

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.dto.UserSuggestion
import cz.city.honest.application.model.dto.UserSuggestionMetadata
import cz.city.honest.application.model.dto.UserSuggestionStateMarking
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.suggestion.SuggestionRepository
import cz.city.honest.application.model.repository.toBoolean
import cz.city.honest.application.model.repository.toInt
import cz.city.honest.application.model.service.RepositoryProvider
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class UserSuggestionRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    val userRepository: UserRepository,
    val suggestionRepositories: Map<String, SuggestionRepository<out Suggestion>>
) : Repository<UserSuggestion>(databaseOperationProvider) {

    override fun insert(userSuggestion: UserSuggestion): Observable<Long> =
        RepositoryProvider.provide(suggestionRepositories, userSuggestion.suggestion::class.java)
            .insert(userSuggestion.suggestion)
            .flatMap { insertUserSuggestion(userSuggestion) }

    private fun insertUserSuggestion(userSuggestion: UserSuggestion) = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            "",
            getContentValues(userSuggestion),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    override fun update(suggestion: UserSuggestion): Observable<Int> {
        TODO("Not yet implemented")
    }

    override fun get(userId: List<String>): Flowable<UserSuggestion> {
        TODO("Not yet implemented")
    }

    fun getNew(userIds: List<String>): Flowable<UserSuggestion> =
        findUserSuggestions(userIds)
            .flatMap { toEntities(it) { toUserSuggestion(it) } }
            .filter { it.metadata.markAs == UserSuggestionStateMarking.NEW }
            .filter { !it.metadata.processed }

    fun getForDelete(userIds: List<String>): Flowable<UserSuggestion> =
        findUserSuggestions(userIds)
            .flatMap { toEntities(it) { toUserSuggestion(it) } }
            .filter { it.metadata.markAs == UserSuggestionStateMarking.DELETE }

    private fun findUserSuggestions(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select suggestion_id, user_id, processed, type, mark_as from user_suggestion where user_id in( ${
                    mapToQueryParamSymbols(
                        userIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(userIds))
            )
        )

    private fun toUserSuggestion(cursor: Cursor) =
        suggestionRepositories[cursor.getString(3)]!!.get(listOf(cursor.getString(0)))
            .flatMap {toUserSuggestion(cursor,it)}

    private fun toUserSuggestion(cursor: Cursor, suggestion: Suggestion) =
        userRepository.get(listOf(cursor.getString(1)))
            .map {
                UserSuggestion(
                    user = it,
                    suggestion = suggestion,
                    metadata = UserSuggestionMetadata(
                        cursor.getInt(2).toBoolean(),
                        UserSuggestionStateMarking.valueOf(cursor.getString(4))
                    )
                )
            }

    override fun delete(suggestion: UserSuggestion): Observable<Int> {
        TODO("Not yet implemented")
    }

    private fun getContentValues(userSuggestion: UserSuggestion) =
        ContentValues().apply {
            put("user_id", userSuggestion.user.id)
            put("suggestion_id", userSuggestion.suggestion.id)
            put("processed", userSuggestion.metadata.processed.toInt())
            put("mark_as", userSuggestion.metadata.markAs.name)
            put("type", userSuggestion.suggestion::class.java.simpleName)
        }

    companion object {
        val TABLE_NAME = "user_suggestion"
    }
}