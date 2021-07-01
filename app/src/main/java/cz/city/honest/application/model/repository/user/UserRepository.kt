package cz.city.honest.application.model.repository.user

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.LoginData
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.autorization.LoginDataRepository
import cz.city.honest.application.model.repository.toBoolean
import cz.city.honest.application.model.service.RepositoryProvider
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class UserRepository(
    operationProvider: DatabaseOperationProvider,
    val loginDataRepositories: Map<String, LoginDataRepository<out LoginData>>
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
                "where id = ?",
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
        put("logged", entity.logged)
        put("login_data_class", entity.loginData.javaClass.simpleName)
    }

    override fun get(id: List<String>): Flowable<User> {
        TODO("Not yet implemented")
    }

    fun get(providerUserId: String, providerDataType: Class<out LoginData>) =
        getLoginDataRepository(providerDataType.simpleName)
            .get(listOf(providerUserId))
            .map { it as LoginData }
            .flatMap { toUser(it) }
            .toList()
            .map { it.first() }

    private fun toUser(loginData: LoginData) =
        findUser(loginData.userId())
            .map { toUser(it,loginData) }

    fun getLoggedUser(): Maybe<User> =
        findLoggedUser()
            .filter { cursorContainsData(it) }
            .flatMap { toUser(it) }

    private fun toUser(cursor: Cursor) = getLoginDataRepository(cursor.getString(4))
        .getByUserId(cursor.getString(0))
        .map { toUser(cursor, it as LoginData) }
        .toMaybe()

    private fun toUser(cursor: Cursor, loginData: LoginData): User {
        return User(
            id = cursor.getString(0),
            username = cursor.getString(2),
            score = cursor.getInt(1),
            logged = cursor.getInt(3).toBoolean(),
            loginData = loginData
        )
    }

    private fun findLoggedUser(): Single<Cursor> =
        Single.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged, login_data_class from user where logged",
                arrayOf()
            )
        )

    private fun findUser(subjectId: String): Flowable<Cursor> =
        Flowable.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged, login_data_class from user where id = ?",
                arrayOf(subjectId)
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