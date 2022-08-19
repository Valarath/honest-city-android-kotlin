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
import cz.city.honest.service.RepositoryProvider
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class UserRepository(
    operationProvider: DatabaseOperationProvider,
    private val loginDataRepositories: Map<String, LoginDataRepository<out LoginData>>
) : Repository<User>(operationProvider) {

    override fun insert(entity: User): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )
        .map { getLoginDataRepository(entity.loginData.javaClass.simpleName) }
        .flatMap { it.insert(entity.loginData) }

    override fun update(entity: User): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "id = ?",
                arrayOf(entity.id)
            )
        )
            .map { getLoginDataRepository(entity.loginData.javaClass.simpleName) }
            .flatMap { it.update(entity.loginData) }

    private fun getLoginDataRepository(loginDataType: String) = RepositoryProvider
        .provideLoginDataRepository(loginDataRepositories, loginDataType)

    private fun getContentValues(entity: User): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("score", entity.score)
        put("username", entity.username)
        put("logged", entity.logged.toInt())
        put("login_data_class", entity.loginData.javaClass.simpleName)
    }

    override fun get(ids: List<String>): Flowable<User> = findUsers(ids)
        .flatMap { toEntities(it) { toUser(it).toFlowable() } }


    fun get(providerUserId: String, providerDataType: Class<out LoginData>) =
        getLoginDataRepository(providerDataType.simpleName)
            .getById(providerUserId)
            .map { it as LoginData }
            .flatMap { toUser(it) }

    private fun toUser(loginData: LoginData) =
        findUser(loginData.userId())
            .filter { cursorContainsData(it) }
            .map { toUser(it,loginData) }

    fun getLoggedUser(): Maybe<User> =
        findLoggedUser()
            .filter { cursorContainsData(it) }
            .flatMap { toUser(it) }

    private fun toUser(cursor: Cursor) = getLoginDataRepository(cursor.getString(4))
        .getByUserId(cursor.getString(0))
        .map { toUser(cursor, it as LoginData) }

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
                "Select id, score, username, logged, login_data_class from user where logged",
                arrayOf()
            )
        )

    private fun findUser(subjectId: String): Maybe<Cursor> =
        Maybe.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged, login_data_class from user where id = ?",
                arrayOf(subjectId)
            )
        )

    private fun findUsers(userIds: List<String>): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged, login_data_class from user where id in( ${
                    mapToQueryParamSymbols(
                        userIds
                    )
                })",
                arrayOf(mapToQueryParamVariable(userIds))
            )
        )


    override fun delete(entity: User): Observable<Int> =
        Observable.just(getLoginDataRepository(entity.loginData.javaClass.simpleName))
            .flatMap { it.delete(entity.loginData) }
            .map {
                databaseOperationProvider.writableDatabase.delete(
                    TABLE_NAME,
                    "id = ?",
                    arrayOf(entity.id)
                )
            }

    companion object {
        const val TABLE_NAME = "user"

    }
}