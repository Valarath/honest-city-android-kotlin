package cz.city.honest.repository.user

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.User
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import cz.city.honest.repository.autorization.LoginDataRepository
import cz.city.honest.repository.toBoolean
import cz.city.honest.repository.toInt
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class UserRepository(
    operationProvider: DatabaseOperationProvider,
    private val loginDataRepository: LoginDataRepository
) : Repository<User>(operationProvider) {

    override fun insert(entity: User): Observable<Long> = Observable.just(insertEntityWithSubscription(entity))
        .map { 1L }

    fun update(entity: User): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "id = ?",
                arrayOf(entity.id)
            )
        )
            .flatMap { loginDataRepository.update(entity.loginData) }

    fun get(ids: List<String>): Flowable<User> = findUsers(ids)
        .flatMap { toEntities(it) { toUser(it).toFlowable() } }

    fun getByUsername(username: String): Maybe<User> = findUserByUsername(username)
        .flatMap { toUser(it) }

    fun get(providerUserId: String) =
        loginDataRepository.getById(providerUserId)
            .map { it }
            .flatMap { toUser(it) }

    fun getLoggedUser(): Maybe<User> =
        findLoggedUser()
            .filter { cursorContainsData(it) }
            .flatMap { toUser(it) }

    private fun insertEntityWithSubscription(entity: User) = loginDataRepository.insert(entity.loginData)
        .map {
            databaseOperationProvider.writableDatabase.insertWithOnConflict(
                TABLE_NAME,
                null,
                getContentValues(entity),
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }.subscribe()

    private fun getContentValues(entity: User): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("score", entity.score)
        put("username", entity.username)
        put("logged", entity.logged.toInt())
    }

    private fun toUser(loginData: LoginData) =
        findUser(loginData.userId())
            .filter { cursorContainsData(it) }
            .map { toUser(it,loginData) }

    private fun toUser(cursor: Cursor) = loginDataRepository
        .getByUserId(cursor.getString(0))
        .map { toUser(cursor, it) }

    private fun toUser(cursor: Cursor, loginData: LoginData): User =
        User(
            id = cursor.getString(0),
            username = cursor.getString(2),
            score = cursor.getInt(1),
            logged = cursor.getInt(3).toBoolean(),
            loginData = loginData
        ).also { cursor.close() }


    private fun findLoggedUser(): Single<Cursor> =
        Single.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged from user where logged",
                arrayOf()
            )
        )

    private fun findUser(subjectId: String): Maybe<Cursor> =
        Maybe.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged from user where id = ?",
                arrayOf(subjectId)
            )
        )

    private fun findUserByUsername(username: String): Maybe<Cursor> =
        Maybe.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged from user where username = ?",
                arrayOf(username)
            )
        )

    private fun findUsers(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged from user where id in( ${
                    mapToQueryParamSymbols(
                        userIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(userIds))
            )
        )

    companion object {
        const val TABLE_NAME = "user"

    }
}