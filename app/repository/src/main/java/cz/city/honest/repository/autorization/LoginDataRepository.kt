package cz.city.honest.repository.autorization

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.fasterxml.jackson.databind.ObjectMapper
import cz.city.honest.dto.LoginData
import cz.city.honest.dto.Suggestion
import cz.city.honest.repository.DatabaseOperationProvider
import cz.city.honest.repository.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

class LoginDataRepository(
    databaseOperationProvider: DatabaseOperationProvider,
    private val objectMapper: ObjectMapper
) : Repository<LoginData>(databaseOperationProvider) {

    fun getByUserId(userId: String): Maybe<LoginData> =
        findLoginDataByUserId(userId)
            .filter { cursorContainsData(it) }
            .flatMap { toLoginData(it) }

    fun getById(id: String): Maybe<LoginData> =
        findLoginDataById(id)
            .filter { cursorContainsData(it) }
            .flatMap { toLoginData(it) }

    fun update(entity: LoginData): Observable<Int> =
        Observable.just(
            databaseOperationProvider.writableDatabase.update(
                TABLE_NAME,
                getContentValues(entity),
                "id = ?",
                arrayOf(entity.getId())
            )
        )

    override fun insert(entity: LoginData): Observable<Long> =
        Observable.just(
            databaseOperationProvider.writableDatabase.insertWithOnConflict(
                TABLE_NAME,
                null,
                getContentValues(entity),
                SQLiteDatabase.CONFLICT_REPLACE
            )
        )

    private fun toLoginData(cursor: Cursor): Maybe<LoginData> = Maybe.just(
        objectMapper.readValue(cursor.getString(3), getClassForName(cursor.getString(2)))
    ).map {
        cursor.close()
        it
    }

    private fun findLoginDataByUserId(userId: String): Maybe<Cursor> =
        Maybe.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, user_id, class, data from login_data where user_id = ?",
                arrayOf(userId)
            )
        )

    private fun findLoginDataById(id: String): Maybe<Cursor> =
        Maybe.just(
            databaseOperationProvider.readableDatabase.rawQuery(
                "Select id, user_id, class, data from login_data where id = ?",
                arrayOf(id)
            )
        )

    private fun getContentValues(entity: LoginData): ContentValues = ContentValues().apply {
        put("id", entity.getId())
        put("user_id", entity.userId())
        put("data", objectMapper.writeValueAsString(entity))
        put("class", entity.javaClass.name)
    }

    companion object {
        const val TABLE_NAME = "login_data"

    }

}