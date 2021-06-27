package cz.city.honest.application.model.repository.user

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import cz.city.honest.application.model.dto.LoginProvider
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.repository.DatabaseOperationProvider
import cz.city.honest.application.model.repository.Repository
import cz.city.honest.application.model.repository.toBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class UserRepository(operationProvider: DatabaseOperationProvider) :
    Repository<User>(operationProvider) {

    override fun insert(entity: User): Observable<Long> = Observable.just(
        databaseOperationProvider.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            getContentValues(entity),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    )

    override fun update(entity: User): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "where id = ?",
                arrayOf(entity.id)
            )
        )

    private fun getContentValues(entity: User): ContentValues = ContentValues().apply {
        put("id", entity.id)
        put("score", entity.score)
        put("username", entity.username)
        put("logged", entity.logged)
        put("login_provider",entity.loginProvider.name)
    }

    override fun get(id: List<String>): Flowable<User> {
        TODO("Not yet implemented")
    }

    fun getLoggedUser(): Single<User> =
        findLoggedUser()
            .map { toUser(it) }

    private fun toUser(cursor: Cursor): User {
        return User(
            id = cursor.getString(0),
            username = cursor.getString(2),
            score = cursor.getInt(1),
            logged = cursor.getInt(3).toBoolean(),
            loginProvider = LoginProvider.valueOf(cursor.getString(4))
        )
    }

    private fun findLoggedUser(): Single<Cursor> =
        Single.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, score, username, logged, login_provider from user where logged",
                arrayOf()
            )
        )


    override fun delete(entity: User): Observable<Int> = Observable.just(
        databaseOperationProvider.writableDatabase.delete(
            TABLE_NAME,
            "id = ?",
            arrayOf(entity.id)
        )
    )

    companion object {
        const val TABLE_NAME = "user"

    }
}